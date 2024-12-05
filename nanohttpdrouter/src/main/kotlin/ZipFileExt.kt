package org.iannewson.httpdrouter

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

// Define a TreeNode to represent directories and files
data class TreeNode(
    val name: String,
    val isDirectory: Boolean,
    var zipEntry: ZipEntry? = null, // Add a ZipEntry reference
    val children: MutableList<TreeNode> = mutableListOf()
) {
    // Add a child node to the current node
    fun addChild(child: TreeNode) {
        children.add(child)
    }
}

// Extension function for ZipFile to build a tree
fun ZipFile.buildZipTree(): TreeNode {
    val root = TreeNode("", true) // Root node representing the top-level directory

    val entries = this.entries().toList() // Convert entries to a list
    for (entry in entries) {
        val parts = entry.name.split("/", "\\")
        var currentNode = root

        for ((index, part) in parts.withIndex()) {
            if (part.isEmpty()) continue // Skip empty parts (e.g., for root entries)

            // Check if a child with the same name already exists
            val existingNode = currentNode.children.find { it.name == part }
            if (existingNode != null) {
                currentNode = existingNode
            } else {
                // Create a new node (directory or file)
                val isDirectory = index < parts.size - 1 || entry.isDirectory
                val newNode = TreeNode(part, isDirectory)
                if (index == parts.size - 1) {
                    newNode.zipEntry = entry // Assign ZipEntry to the leaf node
                }
                currentNode.addChild(newNode)
                currentNode = newNode
            }
        }
    }

    return root
}
