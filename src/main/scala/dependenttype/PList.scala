package dependenttype

sealed abstract class PList[T]
final case class PNil[T]() extends PList[T]
final case class PCons[T](head: T, tail: PList[T])
