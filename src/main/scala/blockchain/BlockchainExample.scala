package blockchain

object BlockchainExample {

trait BlockChainAlgebra[F[_], Block, Blockchain, BlockIndex, Hash] {
  def append(block: Block, to: Blockchain): F[Blockchain]
  def findByIndex(index: BlockIndex, blockchain: Blockchain): F[Blockchain]
  def findByHash(hash: Hash, blockchain:Blockchain): F[Blockchain]
  def commonAncestor(that: Blockchain): F[Block]
  def last: Block
  def length: Int
}






}
