ElevatorListView
================

A ListView extension that causes the top item in the list to be displayed more prominently than the other items in the list. Part of the goal of this project is to have as few additions to the ListView API as possible. So far the only changes that must be done when converting from a normal ListView to an ElevatorListView is to add the collapsedItemHeight and the expandedItemHeight when creating the View.  This is done one of two ways:

If the ElevatorListView is declared in a layout xml:
Add the `xmlns:app="http://schemas.android.com/apk/res-auto"` attribute to the root element of the layout and add `app:collapsedItemHeight` and `app:expandedItemHeight` attributes to the ElevatorListView element. These must be dimension values. E.g.

    <ListView
        android:id="@+id/list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"> 
    </ListView>
    
becomes 

    <com.keyboardr.elevatorlistview.ElevatorListView
        android:id="@+id/list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:collapsedItemHeight="100dp"
        app:expandedItemHeight="300dp"> 
    </com.keyboardr.elevatorlistview.ElevatorListView>
    

If the ElevatorListView is declared in Java, use the constructor `ElevatorListView(Context context, int collapsedItemHeight, int expandedItemHeight)`


No additional changes are needed to your ListAdapter. The `getCollapsedItemHeight()` and `getExpandedItemHeight()` methods are provided for convenience, but there are no corresponding setter methods at this time. These values must be fixed when the ElevatorListView is created.
