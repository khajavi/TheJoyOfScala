package typelevel

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec


/**
  * Created by ikhoon on 2016. 8. 29..
  */
class AuxSpec extends AnyWordSpec with Matchers {

  "What happen when I forgot a refinement" should {
    "misspell" in {
      import Aux._
      import MList._
      assertDoesNotCompile("mdropFirstE2(MNil[Int]())")
      assertDoesNotCompile("mdropFirstE2(MCons[Int](42, MNil[Int]()))")
    }
  }

}
