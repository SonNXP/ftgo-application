package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.kitchenservice.api.TicketDetails;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDomainEvent;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "order_service_restaurants")
/**
 * @Access how JPA provider runtime accesses our entities to persist/load it:
 *         fields or via properties (getters/setters).
 * Note Sometimes you might want to annotate not fields but properties (e.g.
 *       because you want to have some arbitrary logic in the getter or because
 *       you prefer it that way.) In such situation you must define a getter and
 *       annotate it as AccessType.PROPERTY.
 */
@Access(AccessType.FIELD)
public class Restaurant {

  @Id
  private Long id;

  /**
   * @Embeddable annotation to declare that a class will be embedded by other
   *             entities.
   * @Embedded is used to embed a type into another entity.
   */
  @Embedded
  @ElementCollection
  @CollectionTable(name = "order_service_restaurant_menu_items")
  private List<MenuItem> menuItems;
  private String name;

  private Restaurant() {
  }

  public Restaurant(long id, String name, List<MenuItem> menuItems) {
    this.id = id;
    this.name = name;
    this.menuItems = menuItems;
  }

  public List<OrderDomainEvent> reviseMenu(List<MenuItem> revisedMenu) {
    throw new UnsupportedOperationException();
  }

  public void verifyRestaurantDetails(TicketDetails ticketDetails) {
    // TODO - implement me
  }

  public Long getId() {
    return id;
  }

  public Optional<MenuItem> findMenuItem(String menuItemId) {
    return menuItems.stream().filter(mi -> mi.getId().equals(menuItemId)).findFirst();
  }

  public List<MenuItem> getMenuItems() {
    return menuItems;
  }

  public String getName() {
    return name;
  }
}
