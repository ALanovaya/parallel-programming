package trees

interface BST<T : Comparable<T>> {
    suspend fun insert(value: T)
    suspend fun find(value: T): Boolean
    suspend fun remove(value: T)
    suspend fun isValid(): Boolean
}

class Node<T : Comparable<T>>(var value: T, var left: Node<T>? = null, var right: Node<T>? = null)

class Tree<T : Comparable<T>>(var root: Node<T>? = null) : BST<T> {

    override suspend fun insert(value: T) {
        root?.insert(value) ?: run { root = Node(value) }
    }

    private fun Node<T>.insert(value: T) {
        if (value <= this.value) {
            if (left == null) {
                left = Node(value)
            } else {
                left!!.insert(value)
            }
        } else {
            if (right == null) {
                right = Node(value)
            } else {
                right!!.insert(value)
            }
        }
    }

    override suspend fun find(value: T): Boolean {
        return root?.find(value) == true
    }

    private fun Node<T>.find(value: T): Boolean {
        if (this.value == value) {
            return true
        }
        return if (value < this.value) {
            left?.find(value) ?: false
        } else {
            right?.find(value) ?: false
        }
    }

    override suspend fun remove(value: T) {
        root = remove(root, value)
    }

    private fun remove(node: Node<T>?, value: T): Node<T>? {
        if (node == null) {
            return null
        }
        if (value < node.value) {
            node.left = remove(node.left, value)
            return node
        }
        if (value > node.value) {
            node.right = remove(node.right, value)
            return node
        }

        if (node.left == null && node.right == null) {
            return null
        }
        if (node.left == null) {
            return node.right
        }
        if (node.right == null) {
            return node.left
        }
        var leftMostRightSide: Node<T>? = node.right
        while (leftMostRightSide?.left != null) {
            leftMostRightSide = leftMostRightSide.left
        }
        if (leftMostRightSide != null) {
            node.value = leftMostRightSide.value
        }
        node.right = remove(node.right, node.value)
        return node
    }

     override suspend fun isValid(): Boolean {
        return root?.isValid() ?: true
    }

    private fun Node<T>.isValid(): Boolean {
        val leftNode = left
        if (leftNode!= null && leftNode.value > value) {
            return false
        }

        val rightNode = right
        if (rightNode!= null && rightNode.value < value) {
            return false
        }

        return (leftNode?.isValid()?: true) && (rightNode?.isValid()?: true)
    }
}