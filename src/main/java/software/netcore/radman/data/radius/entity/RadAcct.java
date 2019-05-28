package software.netcore.radman.data.radius.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = {"radAcctId", "acctUniqueId"})
@Table(name = "radacct", uniqueConstraints = @UniqueConstraint(name = "acctuniqueid", columnNames = {"acctuniqueid"}))
public class RadAcct {

    @Id
    @Column(name = "radacctid", length = 21)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long radAcctId;

    @Column(name = "acctsessionid", length = 64, nullable = false)
    private String acctSessionId;

    @Column(name = "acctuniqueid", length = 32, nullable = false)
    private String acctUniqueId;

    @Column(length = 64, nullable = false)
    private String username;

    @Column(name = "realm", length = 64)
    private String realm;

    @Column(name = "nasipaddress", length = 15, nullable = false)
    private String nasIpAddress;

    @Column(name = "nasportid", length = 15)
    private String nasPortId;

    @Column(name = "nasporttype", length = 32)
    private String nasPortType;

    @Column(name = "acctstarttime", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date acctStartTime;

    @Column(name = "acctupdatetime", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date acctUpdateTime;

    @Column(name = "acctstoptime", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date acctStopTime;

    @Column(name = "acctinterval", length = 12)
    private Integer acctInterval;

    @Column(name = "acctsessiontime", columnDefinition = "UNSIGNED INT(12)")
    private Integer acctSessionTime;

    @Column(name = "acctauthentic", length = 32)
    private String acctAuthentic;

    @Column(name = "connectinfo_start", length = 50)
    private String connectInfoStart;

    @Column(name = "connectinfo_stop", length = 50)
    private String connectInfoStop;

    @Column(name = "acctinputoctets")
    private Long acctInputOctets;

    @Column(name = "acctoutputoctets")
    private Long acctOutputOctets;

    @Column(name = "calledstationid", length = 50, nullable = false)
    private String calledStationId;

    @Column(name = "callingstationid", length = 50, nullable = false)
    private String callingStationId;

    @Column(name = "acctterminatecause", length = 32, nullable = false)
    private String acctTerminateCause;

    @Column(name = "servicetype", length = 32)
    private String serviceType;

    @Column(name = "framedprotocol", length = 32)
    private String framedProtocol;

    @Column(name = "framedipaddress", length = 15, nullable = false)
    private String framedIpAddress;

}
