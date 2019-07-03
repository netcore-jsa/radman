package software.netcore.radman.buisness.service.accounting.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.netcore.radman.ui.support.Filter;

/**
 * @since v. 1.0.1
 */
@Getter
@Setter
@NoArgsConstructor
public class AccountingFilter extends Filter {

   private boolean searchOnlyActiveSessions = true;

}
