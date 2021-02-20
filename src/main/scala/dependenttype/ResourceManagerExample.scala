package dependenttype

import io.circe.Decoder

object ResourceManagerExample extends App {
  val nfm = new NetworkFileManager
  val rf: nfm.Resource = nfm.create
  nfm.testHash(rf)
  nfm.testDuplicates(rf)

  val nfm2 = new NetworkFileManager
  // doesn't compile
  //  nfm2.testHash(rf)
}


trait ResourceManager {
  type Resource <: BasicResource

  trait BasicResource {
    def hash: String

    def duplicates(r: Resource): Boolean
  }

  def create: Resource

  def testHash(r: Resource) = assert(r.hash == "9e47088d")

  def testDuplicates(r: Resource) = assert(r.duplicates(r))
}

trait FileManager extends ResourceManager {
  type Resource <: File

  trait File extends BasicResource {
    def local: Boolean
  }

  override def create: Resource
}

class NetworkFileManager extends FileManager {
  override type Resource = RemoteFile

  class RemoteFile extends File {
    override def local: Boolean = false

    override def hash: String = "9e47088d"

    override def duplicates(r: RemoteFile): Boolean =
      (local == r.local) && (hash == r.hash)
  }

  override def create: Resource = new RemoteFile
}


