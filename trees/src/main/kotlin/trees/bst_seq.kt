class Node(var value: Int, var left: Node? = null, var right: Node? = null)

class Tree(var root: Node? = null) {

    fun insert(value: Int) {
        root?.insert(value) ?: run { root = Node(value) }
    }

    private fun Node.insert(value: Int) {
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

    fun find(key: Int): Boolean {
        return root?.find(key) == true
    }

    private fun Node.find(key: Int): Boolean {
        if (this.value == key) {
            return true
        }
        return if (key < this.value) {
            left?.find(key) ?: false
        } else {
            right?.find(key) ?: false
        }
    }

    fun remove(key: Int) {
        root = remove(root, key)
    }

    private fun remove(node: Node?, value: Int): Node? {
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
        // key == node.key
        if (node.left == null && node.right == null) {
            return null
        }
        if (node.left == null) {
            return node.right
        }
        if (node.right == null) {
            return node.left
        }
        var leftMostRightSide: Node? = node.right
        while (leftMostRightSide?.left != null) {
            leftMostRightSide = leftMostRightSide.left
        }
        if (leftMostRightSide != null) {
            node.value = leftMostRightSide.value
        }
        node.right = remove(node.right, node.value)
        return node
    }

    fun isValid(): Boolean {
        return root?.isValid() ?: true
    }

    private fun Node.isValid(): Boolean {
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