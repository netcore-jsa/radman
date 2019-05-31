package software.netcore.radman.buisness.service.user.radius.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.netcore.radman.ui.support.Filter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class RadiusUserFilter extends Filter {

    private boolean searchByName;
    private boolean searchByDescription;

    public RadiusUserFilter(String searchText, boolean searchByName, boolean searchByDescription) {
        super(searchText);
        this.searchByName = searchByName;
        this.searchByDescription = searchByDescription;
    }

    public RadiusUserFilter(boolean searchByName, boolean searchByDescription) {
        this.searchByName = searchByName;
        this.searchByDescription = searchByDescription;
    }

}
