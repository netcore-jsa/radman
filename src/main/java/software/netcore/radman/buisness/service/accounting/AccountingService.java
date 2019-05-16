package software.netcore.radman.buisness.service.accounting;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import software.netcore.radman.buisness.service.accounting.dto.AccountingDto;
import software.netcore.radman.data.radius.repo.RadAcctRepo;

import java.util.ArrayList;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class AccountingService {

    private final RadAcctRepo radAcctRepo;
    private final ConversionService conversionService;

    public int countAccountingRecords() {
        return 0;
    }

    public Page<AccountingDto> pageAccountingRecords(Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

}
