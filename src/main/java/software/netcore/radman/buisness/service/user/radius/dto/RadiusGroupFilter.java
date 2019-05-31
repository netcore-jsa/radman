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
public class RadiusGroupFilter extends Filter {

    private boolean searchByGroupName;
    private boolean searchByDescription;

    public RadiusGroupFilter(String searchText, boolean searchByGroupName, boolean searchByDescription) {
        super(searchText);
        this.searchByGroupName = searchByGroupName;
        this.searchByDescription = searchByDescription;
    }

    public RadiusGroupFilter(boolean searchByGroupName, boolean searchByDescription) {
        this.searchByGroupName = searchByGroupName;
        this.searchByDescription = searchByDescription;
    }

}
