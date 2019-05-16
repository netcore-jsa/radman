package software.netcore.radman.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.attribute.AttributeService;
import software.netcore.radman.buisness.service.attribute.dto.AuthenticationAttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AuthorizationAttributeDto;
import software.netcore.radman.ui.menu.MainTemplate;

/**
 * @since v. 1.0.0
 */
@PageTitle("Radman: Attributes")
@Route(value = "attributes", layout = MainTemplate.class)
public class AttributesView extends Div {

    @Autowired
    public AttributesView(AttributeService attributeService) {
        getElement().getStyle().set("display", "flex");
        Div firstSlot = new Div();
        firstSlot.setWidth("700px");
        firstSlot.add(new H2("Authentication attributes"));

        Grid<AuthenticationAttributeDto> authenticationDtoGrid = new Grid<>(AuthenticationAttributeDto.class, false);
        authenticationDtoGrid.setColumns("name", "description", "sensitive");
        DataProvider<AuthenticationAttributeDto, Object> authenticationAttributesDataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> attributeService.pageAuthenticationAttributeRecords(pageable),
                value -> attributeService.countAuthenticationAttributeRecords())
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        authenticationDtoGrid.getColumns().forEach(column -> column.setResizable(true));
        authenticationDtoGrid.setColumnReorderingAllowed(true);
        authenticationDtoGrid.setDataProvider(authenticationAttributesDataProvider);
        authenticationDtoGrid.setWidth("500px");
        firstSlot.add(authenticationDtoGrid);

        Div secondSlot = new Div();
        secondSlot.setWidth("700px");
        secondSlot.add(new H2("Authorization attributes"));

        Grid<AuthorizationAttributeDto> authorizationDtoGrid = new Grid<>(AuthorizationAttributeDto.class, false);
        authorizationDtoGrid.setColumns("name", "description", "sensitive");
        DataProvider<AuthorizationAttributeDto, Object> authorizationAttributesDataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> attributeService.pageAuthorizationAttributeRecords(pageable),
                value -> attributeService.countAuthorizationAttributeRecords())
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        authorizationDtoGrid.getColumns().forEach(column -> column.setResizable(true));
        authorizationDtoGrid.setColumnReorderingAllowed(true);
        authorizationDtoGrid.setDataProvider(authorizationAttributesDataProvider);
        authorizationDtoGrid.setWidth("500px");
        secondSlot.add(authorizationDtoGrid);

        add(firstSlot);
        add(secondSlot);
    }

}
