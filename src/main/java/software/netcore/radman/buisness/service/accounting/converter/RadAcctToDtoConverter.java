package software.netcore.radman.buisness.service.accounting.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.accounting.dto.AccountingDto;
import software.netcore.radman.data.radius.entity.RadAcct;

/**
 * @since v. 1.0.0
 */
public class RadAcctToDtoConverter implements DtoConverter<RadAcct, AccountingDto> {

    @Override
    public AccountingDto convert(RadAcct source) {
        AccountingDto target = new AccountingDto();
        target.setRadAcctId(source.getRadAcctId());
        target.setAcctSessionId(source.getAcctSessionId());
        target.setAcctUniqueId(source.getAcctUniqueId());
        target.setUsername(source.getUsername());
        target.setNasIpAddress(source.getNasIpAddress());
        target.setNasPortId(source.getNasPortId());
        target.setNasPortType(source.getNasPortType());
        target.setAcctStartTime(source.getAcctStartTime());
        target.setAcctUpdateTime(source.getAcctUpdateTime());
        target.setAcctStopTime(source.getAcctStopTime());
        target.setAcctInterval(source.getAcctInterval());
        target.setAcctSessionTime(source.getAcctSessionTime());
        target.setAcctAuthentic(source.getAcctAuthentic());
        target.setConnectInfoStart(source.getConnectInfoStart());
        target.setConnectInfoStop(source.getConnectInfoStop());
        target.setAcctInputOctets(source.getAcctInputOctets());
        target.setAcctOutputOctets(source.getAcctOutputOctets());
        target.setCalledStationId(source.getCalledStationId());
        target.setCallingStationId(source.getCallingStationId());
        target.setAcctTerminateCause(source.getAcctTerminateCause());
        target.setServiceType(source.getServiceType());
        target.setFramedProtocol(source.getFramedProtocol());
        target.setFramedIpAddress(source.getFramedIpAddress());
        return target;
    }

}
