package trees

import kotlinx.coroutines.sync.Mutex

class NodeFine<T : Comparable<T>>(var value: T) {
    var left: NodeFine<T>? = null
    var right: NodeFine<T>? = null
    private val mutex = Mutex()

    suspend fun lock() {
        mutex.lock()
    }

    fun unlock() {
        mutex.unlock()
    }

    suspend fun isValid(): Boolean {
        if (left != null && left!!.value > value) {
            return false
        }
        if (right != null && right!!.value < value) {
            return false
        }
        return left?.isValid()?: true && right?.isValid()?: true
    }
}

class TreeFine<T : Comparable<T>> {
    private var root: NodeFine<T>? = null
    private val mutex = Mutex()

    suspend fun lock() {
        mutex.lock()
    }

    fun unlock() {
        mutex.unlock()
    }

    fun parent_unlock(parent: NodeFine<T>?) {
        if (parent == null) {
            unlock()
        } else {
            parent.unlock()
        }
    }

    suspend fun find_node_parent(value: T): Pair<NodeFine<T>?, NodeFine<T>?> {
        lock()
        if (root == null) {
            return null to null
        }
        root!!.lock()
        var node = root
        var parent: NodeFine<T>? = null
        while (node != null && node.value != value) {
            val grandparent = parent
            parent = node
            if (value < node.value) {
                if (node.left != null) {
                    node.left!!.lock()
                }
                node = node.left
            } else {
                if (node.right != null) {
                    node.right!!.lock()
                }
                node = node.right
            }
            parent_unlock(grandparent)
        }
        return node to parent
    }

    suspend fun insert(value: T) {
        val (node, parent) = find_node_parent(value)
        val new_node = NodeFine(value)
        if (root == null) {
            root = new_node
            unlock()
            return
        }
        parent_unlock(parent)
        if (node != null) {
            node.unlock()
            return
        }
        if (value < parent!!.value) {
            parent.left = new_node
        } else {
            parent.right = new_node
        }
    }

    suspend fun find(value: T): Boolean {
        val (node, parent) = find_node_parent(value)
        parent_unlock(parent)
        if (node != null) {
            node.unlock()
            return true
        }
        return false
    }

    suspend fun remove(value: T) {
        val (node, parent) = find_node_parent(value)
        parent_unlock(parent)
        if (node == null) {
            return
        }

        if (node.left == null && node.right == null) {
            if (node == root) {
                root = null
            } else if (value < parent!!.value) {
                parent.left = null
            } else {
                parent.right = null
            }
            return
        }

        if (node.left == null) {
            if (node == root) {
                root = node.right
            } else if (value < parent!!.value) {
                parent.left = node.right
            } else {
                parent.right = node.right
            }
            return
        }

        if (node.right == null) {
            if (node == root) {
                root = node.left
            } else if (value < parent!!.value) {
                parent.left = node.left
            } else {
                parent.right = node.left
            }
            return
        }
        node.unlock()
        node.right!!.lock()
        var succParent = node
        var succ = node.right
        while (succ!!.left!= null) {
            val succGrandparent = succParent
            succParent = succ
            succ.left!!.lock()
            succ = succ.left
            if (succGrandparent!= null && succGrandparent!= node) {
                succGrandparent.unlock()
            }
        }
        if (succParent != node) {
            succParent?.unlock()
            succParent?.left = succ.right
        } else {
            succParent.right = succ.right
        }
        node.value = succ.value
    }

    suspend fun isValid(): Boolean {
        return root?.isValid()?: true
    }
}
