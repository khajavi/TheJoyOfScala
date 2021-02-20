package optics

import monocle.{Lens, Optional}
import monocle.macros.GenLens

object OpticsExample extends App {

  def updateStockWithoutLenses(user: User): User = {
    user.copy(
      cart = user.cart.copy(
        item = user.cart.item.copy(leftInStock = user.cart.item.leftInStock - 1)
      )
    )
  }

  def updateStockWithLenses(user: User): User = {
    val cart: Lens[User, Cart] = GenLens[User](_.cart)
    val item: Lens[Cart, Item] = GenLens[Cart](_.item)
    val leftInStock: Lens[Item, Int] = GenLens[Item](_.leftInStock)
    (cart composeLens item composeLens leftInStock).modify(_ - 1)(user)
  }

  def getDiscountValue(discount: Discount): Option[Double] = {
    val maybeDiscountValue = Optional[Discount, Double] {
      case pctOff: PercentageOff => Some(pctOff.value)
      case fixOff: FixPriceOff   => Some(fixOff.value)
      case _                     => None
    } { discountValue =>
      {
        case pctOff: PercentageOff => pctOff.copy(value = discountValue)
        case fixOff: FixPriceOff   => fixOff.copy(value = discountValue)
        case discount              => discount
      }
    }

    maybeDiscountValue.getOption(discount)
  }

}

case class User(name: String, cart: Cart)
case class Cart(id: String, item: Item, quantity: Int)
case class Item(
    sku: String,
    price: Double,
    leftInStock: Int,
    discount: Discount
)

trait Discount
case class NoDiscount() extends Discount
case class PercentageOff(value: Double) extends Discount
case class FixPriceOff(value: Double) extends Discount
