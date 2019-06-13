# RadMan
RadMan is a FreeRadius DB Manager - an easy to use FreeRadius Management GUI.  
Brought to you free and open-source by NetCore j.s.a., the company behind [Unimus](https://unimus.net/).

![](https://github.com/netcore-jsa/radman/raw/master/RadMan-small.png)

# Table of Contents
- [What is RadMan](#what-is-radman)
- [How does RadMan work](#how-does-radman-work)
- [How to deploy RadMan](#how-to-deploy-radman)
- [How to upgrade RadMan](#how-to-upgrade-radman)
- [RadMan config file](#radman-config-file)

# What is RadMan
Simply, RadMan is a FreeRadius Management GUI.  
We wrote RadMan because we were not happy with existing FreeRadius management options.

RadMan does **not** manage FreeRadius itself (it does not touch the FreeRadius config files).  
What RadMan offers is an easy way to manage a FreeRadius DB in a web interface.

RadMan aims to be simple to use, super fast to deploy and easy to maintain.

# How does RadMan work
RadMan works by inserting / updating / removing records from the FreeRadius database tables.  
RadMan also requires a database itself, for storing it's own data.

RadMan therefore initiates 2 DB connections:
- to the Radius database
- to it's own database

### Radius DB
For the Radius DB, RadMan manipulates all required tables to manage a Radius database:  
`nas`, `radacct`, `radcheck`, `radgroupcheck`, `radgroupreply`, `radhuntgroup`, `radreply`, `radusergroup`

Just by deploying RadMan and pointing it at a working FreeRadius database, you will be able to see a list of NASes (`nas` table), NAS groups (`radhuntgroup` table), user/group mappings (`radusergroup` table) and the accounting table (`radacct`).

To see records from `radcheck`, `radgroupcheck` and `radreply` and `radgroupreply`, you will have to tell RadMan which Radius Attributes you want it to manage.  
If you want RadMan to simply manage everything you already have in your Radius deploy, click `Load from Radius` for both `Authentication attributes` and `Authorization attributes` in the `Attributes` menu.

After this, you can check the `Auth (AA)` menu in the `Radius` section, and you should see full Attribute mappings for your users and groups (as mentioned, these come from `radcheck`, `radgroupcheck`, `radreply` and `radgroupreply` tables).

To manage Users and Groups, you will want to tell RadMan which Users and Groups it should manage.  
As previously, to simply manage everything you already have in your Radius deploy, click `Load from Radius` in the `Users` and `Groups` menus.

You will now be able to fully manage Radius users and groups in RadMan.  
This enables full attribute assignments for Autorization (`radcheck` and `radgroupcheck` table) and Authorization (`radreply` and `radgroupreply` table) under the `Auth (AA)` menu.  
You can now also manage group memberships for users in the `User/Group` menu.

### RadMan DB
By using the various menus from the `Radius` category, you can manage the Radius DB.  
In the `RadMan` menu category, you are in turn managing the `RadMan` database.

The RadMan database has 3 purposes:

It tells RadMan which of your Radius Users/Groups/Attributes it should manage.  
(see [Radius DB](#radius-db) section for more details)

It acts as a repository for all Users/Groups/Attributes you can use/configure in RadMan.  
Even if these Users/Groups/Attributes are not present in the Radius DB directly, you can still add them into the Radius DB from RadMan (if we didn't store them in a DB somewhere, they would completely disappear from RadMan if removed from the Radius DB).

It allows you to delete entities globally from the Radius DB.  
For example, when deleting a `User` in `Users` menu in RadMan, you have the option `Remove from Radius`. This would remove this user from all appropriate tables in the Radius DB - `radcheck`, `radreply` and `radusergroup`.  
No more having to crawl through tables manually, or forgetting to clean something up!

As another example, when deleting an `Attribute` in the `Attributes` menu in RadMan, and checking `Remove from Radius`, that attribute will automatically be removed from all records in the `radcheck`, `radgroupcheck`, `radreply` and `radgroupreply` tables. This makes it super-easy and super-fast to remove no-longer used attributes withouth crawling through all the tables yourself.

Please note that User / Group deletions will **not** delete records from the Accounting (`radacct`) table.

# How to deploy RadMan
Before you deploy RadMan, you will need a Java Runtime Environment.  
You can install `openjdk-8-jre` or `openjdk-11-jre` - whichever is available for your Linux distribution.  
Use `apt-get install ...` or `yum install ...` or whatever is appropriate for your environment.

Before you proceed further, please make sure that `java -version` works, and returns the expected Java version.

Now download a release binary from our [GitHub Releases](https://github.com/netcore-jsa/radman/releases).  
After that, run:
```
mkdir /opt/radman
mkdir /etc/radman

unzip radman*.zip

mv RadMan.jar /opt/radman/RadMan.jar
mv -i radman.properties.example /etc/radman/radman.properties
mv -i radman.default /etc/default/radman
mv -i radman.service /etc/systemd/system/radman.service

systemctl daemon-reload
```

After this, you will want to adjust the configuration file.  
Check the [How does RadMan work](#how-does-radman-work) and [RadMan config file](#radman-config-file) sections for more info.  
(use your favorite editor like `nano` instead of `vim` if you wish)
```
vim /etc/radman/radman.properties
```

After the config file is properly setup, start RadMan:
```
systemctl enable radman
systemctl start radman
```

You can check the log file to see if everything is running:
```
tail -f /var/log/radman
```

You should now see RadMan running at `http://your-server-ip:8089`.  
You may need to adjust `iptables` or other firewalls to allow connections to `8089` on your server.

When connecting to RadMan for the first time, there will be no users in it's user database.  
Check the log file for one-time login credentials so you can perform the first login (`tail -f /var/log/radman`).  
After the first login, generate your RadMan users in the `System users` menu.

# How to upgrade RadMan
Download a new release binary from our [GitHub Releases](https://github.com/netcore-jsa/radman/releases).  
You will want to extract `RadMan.jar` from the release package.

Now, just replace `/opt/radman/RadMan.jar` with the newer version and restart the service.  
Example:
```
systemctl stop radman
mv RadMan-new.jar /opt/radman/RadMan.jar
systemctl start radman
```

# RadMan config file
The RadMan config file (`radman.properties`) should be fairly straight-forward to understand.
We ship and example file in the `config-files` directory, which you can adjust for actual RadMan deployments.

From the example file, you will want to adjust 4 values enclosed in `[...]` (square-brackets) for both the `radius database` and the `internal database` sections.

The `radius database` section should point to a fully configured Radius database.  
The `internal database` section should point to a database that RadMan itself will use.  
(this should be empty - RadMan will initialize it automatically on it's first run)

RadMan also allows user auth into RadMan itself using LDAP.  
You should configure the appropriate settings in the `ldap` section if you wish to use this.
