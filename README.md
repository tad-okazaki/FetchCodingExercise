My name is Tad Okazaki, and this is the Fetch Rewards Coding Exercise.


My completed work is on the master branch.

This exercise was done in several parts, and each branch represents a change to the project. This is the order I created the branches in, and what each contributed to the project:

1. main: After creating the repository, I made the "main" branch so I could keep my master branch clean until ready to show my work. Once "ui-alignment-fixes" was merged with main, I merged main into master.
2. display-items: I then branched off of main, and made the branch "display-items", which added json reading functionality, a data class called AmazonData to hold the json format, and a basic list view Composable to display the data
3. group-by-list-id: After merging "display-items" back into main, I created the branch "group-by-list-id", which added the function groupByListId() to sort the list by listId. This function later became unused because I found a better way to sort, but it was added with this PR.
4. sort-by-name: After merging "group-by-list" back into main, I created the branch "sort-by-name", which added sortList(), which will sort by listId, then by name. I added removeItemPrefix(), which will allow the prefix "Item " to be removed from the name, so the items can be compared as ints. I removed usage of groupByListId(), because I found a simpler way to sort by listId.
5. remove-blank-or-null-names: After merging "sort-by-name" back into main, I created the branch "remove-blank-or-null-names", which changed sortList() to remove null or blank name entries.
6. ui-alignment-fixes: After merging "remove-blank-or-null-names" back into main, I created the branch "ui-alignment-fixes", which changed AmazonListView() Composable to have a header row for readability. I also added AmazonItem() Composable to add rows, increased font sizes for readability, and added comments for easier analysis.

I made a pull request on Github for each major change/requirement, so my progress could be easier tracked.

I'm very excited for the opportunity to work for Fetch Rewards, and I hope to be in contact with you soon!

I can be reached at tokazaki14@gmail.com.

Thank you!
