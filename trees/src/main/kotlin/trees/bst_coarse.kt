package trees

import kotlinx.coroutines.sync.Mutex

class TreeCoarse<T : Comparable<T>> : BST<T>  {
    private val tree: Tree<T> = Tree()
    private val mutex = Mutex()

    override suspend fun insert(value: T) {
        mutex.lock()
        try {
            tree.insert(value)
        } finally {
            mutex.unlock()
        }
    }

    override suspend fun remove(value: T) {
        mutex.lock()
        try {
            tree.remove(value)
        } finally {
            mutex.unlock()
        }
    }

    override suspend fun find(value: T): Boolean {
        mutex.lock()
        try {
            return tree.find(value)
        } finally {
            mutex.unlock()
        }
    }

    override suspend fun isValid(): Boolean {
        mutex.lock()
        try {
            return tree.isValid()
        } finally {
            mutex.unlock()
        }
    }
}