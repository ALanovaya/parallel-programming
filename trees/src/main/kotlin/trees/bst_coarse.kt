package trees

import kotlinx.coroutines.sync.Mutex

class Tree<T : Comparable<T>> {
    private val tree: Tree<T> = Tree()
    private val mutex = Mutex()

    suspend fun insert(data: T) {
        mutex.lock()
        try {
            tree.insert(data)
        } finally {
            mutex.unlock()
        }
    }

    suspend fun remove(data: T) {
        mutex.lock()
        try {
            tree.remove(data)
        } finally {
            mutex.unlock()
        }
    }

    suspend fun find(data: T): Boolean {
        mutex.lock()
        try {
            return tree.find(data)
        } finally {
            mutex.unlock()
        }
    }

    suspend fun isValid(): Boolean {
        mutex.lock()
        try {
            return tree.isValid()
        } finally {
            mutex.unlock()
        }
    }
}