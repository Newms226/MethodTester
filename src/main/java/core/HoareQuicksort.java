//https://www.geeksforgeeks.org/hoares-vs-lomuto-partition-scheme-quicksort/
package core;

public class HoareQuicksort {
    static void swap(int[] array,
                     int position1,
                     int position2)
    {
        // Swaps elements in an array

        // Copy the first position's element
        int temp = array[position1];

        // Assign to the second element
        array[position1] = array[position2];

        // Assign to the first element
        array[position2] = temp;
    }

    /* This function takes last element as
    pivot, places the pivot element at its
    correct position in sorted array, and
    places all smaller (smaller than pivot)
    to left of pivot and all greater elements
    to right of pivot */
    static int partition(int []arr, int low,
                         int high)
    {
        int pivot = arr[high];

        // Index of smaller element
        int i = (low - 1);

        for (int j = low; j <= high- 1; j++)
        {
            // If current element is smaller
            // than or equal to pivot
            if (arr[j] <= pivot)
            {
                i++; // increment index of
                // smaller element
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return (i + 1);
    }

    /* The main function that
       implements QuickSort
    arr[] --> Array to be sorted,
    low --> Starting index,
    high --> Ending index */
    static void quickSort(int []arr, int low,
                          int high)
    {
        if (low < high)
        {
        /* pi is partitioning index,
        arr[p] is now at right place */
            int pi = partition(arr, low, high);

            // Separately sort elements before
            // partition and after partition
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    static void quickSort(int[] arr) {
        int n = arr.length;
        quickSort(arr, 0, n-1);
    }

    /* Function to print an array */
    static void printArray(int []arr, int size)
    {
        int i;
        for (i = 0; i < size; i++)
            System.out.print(" " + arr[i]);
        System.out.println();
    }
//
//    // Driver Code
//    static public void main (String[] args)
//    {
//        int []arr = {10, 7, 8, 9, 1, 5};
//        int n = arr.length;
//        quickSort(arr, 0, n-1);
//        System.out.println("Sorted array: ");
//        printArray(arr, n);
//    }
}
