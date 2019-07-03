package software.netcore.radman.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.exception.NotFoundException;
import software.netcore.radman.buisness.service.accounting.AccountingService;
import software.netcore.radman.buisness.service.accounting.dto.AccountingDto;
import software.netcore.radman.buisness.service.accounting.dto.AccountingFilter;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.menu.MainTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("RadMan: Accounting")
@Route(value = "tutorial", layout = MainTemplate.class)
public class AccountingView extends VerticalLayout {

    private final AccountingFilter filter = new AccountingFilter();
    private final AccountingService accountingService;

    @Autowired
    public AccountingView(AccountingService accountingService) {
        this.accountingService = accountingService;
        buildView();
    }

    private void buildView() {
        setHeightFull();
        setSpacing(false);

        Grid<AccountingDto> grid = new Grid<>(AccountingDto.class, false);
        grid.addColumns("username", "callingStationId", "nasIpAddress", "serviceType");
        grid.addColumn(new LocalDateTimeRenderer<>((ValueProvider<AccountingDto, LocalDateTime>)
                accountingDto -> {
                    if (Objects.isNull(accountingDto.getAcctStartTime())) {
                        return null;
                    }
                    return LocalDateTime.ofInstant(accountingDto.getAcctStartTime().toInstant(),
                            TimeZone.getDefault().toZoneId());
                })).setSortProperty("acctStartTime").setHeader("Acct Start Time");
        grid.addColumn(new LocalDateTimeRenderer<>((ValueProvider<AccountingDto, LocalDateTime>)
                accountingDto -> {
                    if (Objects.isNull(accountingDto.getAcctStopTime())) {
                        return null;
                    }
                    return LocalDateTime.ofInstant(accountingDto.getAcctStopTime().toInstant(),
                            TimeZone.getDefault().toZoneId());
                })).setSortProperty("acctStopTime").setHeader("Acct Stop Time");
        grid.addColumns("acctTerminateCause", "framedIpAddress", "framedProtocol");
        grid.addColumns("acctAuthentic", "acctInputOctets", "acctInterval", "acctOutputOctets",
                "acctSessionId");
        grid.addColumn(accountingDto -> DurationFormatUtils.formatDurationHMS(accountingDto.getAcctSessionTime()))
                .setSortProperty("acctSessionTime").setHeader("Acct Session Time");
        grid.addColumns("acctUniqueId", "acctUpdateTime", "calledStationId",
                "connectInfoStart", "connectInfoStop", "nasPortId", "nasPortType", "radAcctId", "realm");

        DataProvider<AccountingDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> accountingService.pageAccountingRecords(filter, pageable),
                value -> accountingService.countAccountingRecords(filter))
                .withDefaultSort("radAcctId", SortDirection.ASCENDING)
                .build();
        grid.setDataProvider(dataProvider);
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setColumnReorderingAllowed(true);
        grid.setMinHeight("500px");
        grid.setHeight("100%");

        TextField search = new TextField(event -> {
            filter.setSearchText(event.getValue());
            grid.getDataProvider().refreshAll();
        });
        search.setValueChangeMode(ValueChangeMode.EAGER);
        search.setPlaceholder("Search...");

        SetAcctStopTimeDialog setAcctStopTimeDialog = new SetAcctStopTimeDialog((source, bean)
                -> grid.getDataProvider().refreshItem(bean));
        Button setAcctStopTimeButton = new Button("Set Acct Stop Time", event -> {
            Optional<AccountingDto> optional = grid.getSelectionModel().getFirstSelectedItem();
            optional.ifPresent(setAcctStopTimeDialog::set);
        });
        setAcctStopTimeButton.setEnabled(false);

        grid.asSingleSelect().addValueChangeListener(event
                -> setAcctStopTimeButton.setEnabled(Objects.nonNull(event.getValue())));

        Checkbox onlyActiveSessions = new Checkbox("Filter only active sessions");
        onlyActiveSessions.setValue(filter.isSearchOnlyActiveSessions());
        onlyActiveSessions.addValueChangeListener(event -> {
            filter.setSearchOnlyActiveSessions(event.getValue());
            grid.getDataProvider().refreshAll();
        });

        add(new H4("Data from Radius DB - \"radacct\" table"));
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        horizontalLayout.add(new H3("Accounting"));
        horizontalLayout.add(setAcctStopTimeButton);
        horizontalLayout.add(search);
        horizontalLayout.add(onlyActiveSessions);
        add(horizontalLayout);
        add(grid);
    }

    private class SetAcctStopTimeDialog extends Dialog {

        private AccountingDto accountingDto;
        private final TextField timestampField;
        private final Checkbox checkboxNow;

        SetAcctStopTimeDialog(UpdateListener<AccountingDto> updateListener) {
            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.setPadding(false);
            verticalLayout.setMargin(false);

            timestampField = new TextField("Timestamp");
            timestampField.setErrorMessage("Timestamp number is required");
            timestampField.setValueChangeMode(ValueChangeMode.EAGER);
            timestampField.addValueChangeListener(event -> isValid(event.getValue()));

            checkboxNow = new Checkbox("Now");
            Button setBtn = new Button("Set");
            Button cancelBtn = new Button("Cancel", event -> setOpened(false));

            checkboxNow.addValueChangeListener(event -> {
                if (event.getValue()) {
                    timestampField.setValue(String.valueOf(Instant.now().getEpochSecond()));
                }
                timestampField.setEnabled(!event.getValue());
            });
            timestampField.addValueChangeListener(event -> setBtn.setEnabled(Objects.nonNull(event.getValue())));

            setBtn.setEnabled(false);
            setBtn.addClickListener(event -> {
                if (isValid(timestampField.getValue())) {
                    try {
                        long timestamp = Long.valueOf(timestampField.getValue());
                        AccountingDto updatedAccountingDto = accountingService.setAcctStopTime(accountingDto,
                                new Date(timestamp * 1000));
                        updateListener.onUpdated(this, updatedAccountingDto);
                        setOpened(false);
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            HorizontalLayout controls = new HorizontalLayout();
            controls.add(cancelBtn, setBtn);
            verticalLayout.add(new H3("Set Acct Stop Time"), timestampField, checkboxNow, new Hr(), controls);
            verticalLayout.setHorizontalComponentAlignment(Alignment.END, controls);
            add(verticalLayout);

        }

        private boolean isValid(String value) {
            try {
                Long.valueOf(value);
                timestampField.setInvalid(false);
                return true;
            } catch (NumberFormatException e) {
                timestampField.setInvalid(true);
                return false;
            }
        }

        void set(AccountingDto accountingDto) {
            this.accountingDto = accountingDto;
            setOpened(true);
            checkboxNow.setValue(false);
            timestampField.clear();
            timestampField.setInvalid(false);
        }

    }

}


