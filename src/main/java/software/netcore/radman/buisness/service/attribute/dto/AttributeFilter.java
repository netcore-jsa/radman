package software.netcore.radman.buisness.service.attribute.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.netcore.radman.ui.support.Filter;

/**
 * @since v. 1.0.0
 */
@Setter
@Getter
@NoArgsConstructor
public class AttributeFilter extends Filter {

    private boolean searchByName;
    private boolean searchByDescription;

    public AttributeFilter(String searchText, boolean searchByName, boolean searchByDescription) {
        super(searchText);
        this.searchByName = searchByName;
        this.searchByDescription = searchByDescription;
    }

    public AttributeFilter(boolean searchByName, boolean searchByDescription) {
        this.searchByName = searchByName;
        this.searchByDescription = searchByDescription;
    }

}
