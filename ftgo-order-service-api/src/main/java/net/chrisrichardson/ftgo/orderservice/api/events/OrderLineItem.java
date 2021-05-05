package net.chrisrichardson.ftgo.orderservice.api.events;

import net.chrisrichardson.ftgo.common.Money;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * Specifies a class whose instances are stored as an intrinsic part of an
 * owning entity and share the identity of the entity. Each of the persistent
 * properties or fields of the embedded object is mapped to the database table
 * for the entity.
 * 
 * Note that the Transient annotation may be used to designate the
 * non-persistent state of an embeddable class.
 */
@Embeddable
public class OrderLineItem {

  public OrderLineItem() {
  }

  private int quantity;
  private String menuItemId;
  private String name;

  @Embedded
  @AttributeOverrides(@AttributeOverride(name = "amount", column = @Column(name = "price")))
  private Money price;

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  /**
   * https://stackoverflow.com/questions/10912646/hashcodebuilder-and-equalsbuilder-usage-style
   */
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  public OrderLineItem(String menuItemId, String name, Money price, int quantity) {
    this.menuItemId = menuItemId;
    this.name = name;
    this.price = price;
    this.quantity = quantity;
  }

  public Money deltaForChangedQuantity(int newQuantity) {
    return price.multiply(newQuantity - quantity);
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public void setMenuItemId(String menuItemId) {
    this.menuItemId = menuItemId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(Money price) {
    this.price = price;
  }

  public int getQuantity() {
    return quantity;
  }

  public String getMenuItemId() {
    return menuItemId;
  }

  public String getName() {
    return name;
  }

  public Money getPrice() {
    return price;
  }

  public Money getTotal() {
    return price.multiply(quantity);
  }

}
