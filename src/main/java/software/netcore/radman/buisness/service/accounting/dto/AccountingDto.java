package software.netcore.radman.buisness.service.accounting.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
public class AccountingDto {

    private Long radAcctId;
    private String acctSessionId;
    private String acctUniqueId;
    private String username;
    private String realm;
    private String nasIpAddress;
    private String nasPortId;
    private String nasPortType;
    private Date acctStartTime;
    private Date acctUpdateTime;
    private Date acctStopTime;
    private Integer acctInterval;
    private Integer acctSessionTime;
    private String acctAuthentic;
    private String connectInfoStart;
    private String connectInfoStop;
    private Long acctInputOctets;
    private Long acctOutputOctets;
    private String calledStationId;
    private String callingStationId;
    private String acctTerminateCause;
    private String serviceType;
    private String framedProtocol;
    private String framedIpAddress;

}
