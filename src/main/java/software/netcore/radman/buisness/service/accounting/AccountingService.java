package software.netcore.radman.buisness.service.accounting;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import software.netcore.radman.buisness.service.accounting.dto.AccountingDto;
import software.netcore.radman.data.radius.entity.QRadAcct;
import software.netcore.radman.data.radius.entity.RadAcct;
import software.netcore.radman.data.radius.repo.RadAcctRepo;
import software.netcore.radman.ui.support.Filter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class AccountingService {

    private final RadAcctRepo radAcctRepo;
    private final ConversionService conversionService;

    public long countAccountingRecords(Filter filter) {
        return radAcctRepo.count(buildAccountingSearchPredicate(filter));
    }

    public Page<AccountingDto> pageAccountingRecords(Filter filter, Pageable pageable) {
        Page<RadAcct> page = radAcctRepo.findAll(buildAccountingSearchPredicate(filter), pageable);
        List<AccountingDto> accountingDtos = page.stream()
                .map(radAcct -> conversionService.convert(radAcct, AccountingDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(accountingDtos, pageable, accountingDtos.size());
    }

    private Predicate buildAccountingSearchPredicate(Filter filter) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!(StringUtils.isEmpty(filter.getSearchText()))) {
            booleanBuilder.or(QRadAcct.radAcct.acctSessionId.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.acctUniqueId.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.username.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.realm.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.nasIpAddress.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.nasPortId.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.nasPortType.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.acctInterval.stringValue().contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.acctSessionTime.stringValue().contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.acctAuthentic.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.connectInfoStart.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.connectInfoStop.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.acctInputOctets.stringValue().contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.acctOutputOctets.stringValue().contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.calledStationId.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.callingStationId.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.acctTerminateCause.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.serviceType.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.framedProtocol.contains(filter.getSearchText()));
            booleanBuilder.or(QRadAcct.radAcct.framedIpAddress.contains(filter.getSearchText()));
        }
        return booleanBuilder.getValue();
    }

}
