package trie;

/**
 * Created by bruce_shan on 2018/12/4 20:33.
 * Corporation CSU Software
 */
public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Inserts a word into the trie.
    public void insert(String word) {
        TrieNode node = root;
        int length = word.length();
        int position ;
        char c;
        for (int i = 0; i < length; i++) {
            c = word.charAt(i);
            position = c-'a';
            if (node.trieNodes[position] == null) {
                node.trieNodes[position] = new TrieNode();
            }
            node = node.trieNodes[position];
            node.setCount(node.getCount()+1);
        }
        node.setExist(true);
    }

    // Returns if the word is in the trie.
    public boolean search(String word) {
        boolean result = false;
        TrieNode node = root;
        int length = word.length();
        int position ;
        char c;
        for (int i = 0; i < length; i++) {
            c = word.charAt(i);
            position = c - 'a';
            node = node.trieNodes[position];
            if (node == null) {
                break;
            }
        }
        if (node != null && node.getExist()) {
            result = true;
        }
        return result;
    }

    // Returns if there is any word in the trie
    // that starts with the given prefix.
    public boolean startsWith(String prefix) {
        TrieNode node = root;
        int length = prefix.length();
        int position ;
        char c;
        for (int i = 0; i < length; i++) {
            c = prefix.charAt(i);
            position = c - 'a';
            node = node.trieNodes[position];
            if (node == null) {
                return false;
            }
        }
        return true;
    }

    // delete if the word is in the trie.
    public boolean doDelete(String word, TrieNode node) {
        //树中已匹配的字符串比传入字符串短
        if (node == null) {
            return false;
        }

        //树中已匹配的字符串比传入字符串不短
        if (word.length()  > 1){
            char c = word.charAt(0);
            int position = c - 'a';
            TrieNode trieNode = node.trieNodes[position];
            boolean b = doDelete(word.substring(1), trieNode);
            if (b) {
                node.setCount(node.getCount() - 1);
                if (trieNode.getCount() == 0) {
                    node.trieNodes[position] = null;
                }
                return true;
            }
        }

        if (word.length() == 1) {
            char c = word.charAt(0);
            int position = c - 'a';
            TrieNode trieNode = node.trieNodes[position];
            //只删除单词 如果是前缀不删除
            if (trieNode != null && trieNode.getExist()) {
                return true;
            }
        }
        return false;
    }

    // delete if the word is in the trie.
    public boolean delete(String word) {
        return this.doDelete(word,root);
    }

    class TrieNode {
        // Initialize your data structure here.
        int count = 0;
        TrieNode[] trieNodes = new TrieNode[26];
        Boolean exist = false;
        public TrieNode() {
        }

        public TrieNode(int count, Boolean exist) {
            this.count = count;
            this.exist = exist;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public TrieNode[] getTrieNodes() {
            return trieNodes;
        }

        public void setTrieNodes(TrieNode[] trieNodes) {
            this.trieNodes = trieNodes;
        }

        public Boolean getExist() {
            return exist;
        }

        public void setExist(Boolean exist) {
            this.exist = exist;
        }
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.search("lintcode");
        trie.startsWith("lint");
        trie.insert("lint");
        trie.startsWith("lint");

        boolean lint = trie.delete("lin");
        System.out.println("lint = " + lint);
        lint = trie.delete("lint");
        System.out.println("lint = " + lint);
    }
}
