package trees

import kotlinx.coroutines.sync.Mutex

class NodeOpt<T : Comparable<T>>(
    var value: T,
    var left: NodeOpt<T>? = null,
    var right: NodeOpt<T>? = null,
    private val mutex: Mutex = Mutex()
) {
    suspend fun lock() {
        mutex.lock()
    }

    fun unlock() {
        mutex.unlock()
    }

    fun isValid(): Boolean {
        if (left != null && left!!.value > value) {
            return false
        }
        if (right != null && right!!.value < value) {
            return false
        }
        return left?.isValid() ?: true && right?.isValid() ?: true
    }
}

class TreeOpt<T : Comparable<T>>(
    private var root: NodeOpt<T>? = null,
    private val mutex: Mutex = Mutex()
) {
    suspend fun lock() {
        mutex.lock()
    }

    fun unlock() {
        mutex.unlock()
    }

    fun validate(value: T, nd: NodeOpt<T>?, parent: NodeOpt<T>?): Boolean {
        if (nd == null && parent == null) {
            return root == null
        }
        var curr = root
        var prev: NodeOpt<T>? = null
        while (curr != null && curr.value != value && curr != nd) {
            prev = curr
            if (value < curr.value) {
                curr = curr.left
            } else {
                curr = curr.right
            }
        }
        return curr == nd && prev == parent
    }

    suspend fun find_node_parent(value: T): Pair<NodeOpt<T>?, NodeOpt<T>?> {
        while (true) {
            lock()
            if (root == null) {
                return null to null
            }
            var current = root
            var parent: NodeOpt<T>? = null
            while (current != null && current.value != value) {
                val grandparent = parent
                parent = current
                if (value < current.value) {
                    current = current.left
                } else {
                    current = current.right
                }
                if (grandparent == null) {
                    unlock()
                }
            }
            parent?.lock()
            current?.lock()
            if (validate(value, current, parent)) {
                return current to parent
            }
            current?.unlock()
            parent?.unlock()
        }
    }

    suspend fun insert(value: T) {
        var nd: NodeOpt<T>? = null
        var parent: NodeOpt<T>? = null
        find_node_parent(value).let { nd = it.first; parent = it.second }
        if (root == null) {
            root = NodeOpt(value)
            unlock()
            return
        }
        if (nd == null) {
            if (value < parent!!.value) {
                parent?.left = NodeOpt(value)
            } else {
                parent?.right = NodeOpt(value)
            }
        }
        parent?.unlock()
    }

    suspend fun find(data: T): Boolean {
        var nd: NodeOpt<T>? = null
        var parent: NodeOpt<T>? = null
        find_node_parent(data).let { nd = it.first; parent = it.second }
        if (nd == null) {
            return false
        }
        val tempNd = nd
        tempNd?.unlock()
        parent?.unlock()

        return true
    }

    fun isValid(): Boolean {
        return root?.isValid() ?: true
    }
}