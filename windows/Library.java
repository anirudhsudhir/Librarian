import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Library {

    String arr[][];
    int cols = 7, found = 0;
    Scanner sc = new Scanner(System.in);

    void fileToArray() throws IOException {
        Scanner fr = new Scanner(new FileReader("C:\\Library Software\\File.txt"));
        int count = 0;
        while (fr.hasNextLine()) {
            count++;
            fr.nextLine();
        }
        arr = new String[count][cols];
        fr.close();
        count = 0;
        Scanner fr2 = new Scanner(new FileReader("C:\\Library Software\\File.txt"));
        while (fr2.hasNextLine()) {
            StringTokenizer str = new StringTokenizer(fr2.nextLine(), ",");
            for (int i = 0; i < cols; i++)
                arr[count][i] = str.nextToken();
            count++;
        }
        fr2.close();
    }

    void arrayToFile(String[][] temparr, FileWriter fw) throws IOException {
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        for (int i = 0; i < temparr.length; i++) {
            for (int j = 0; j < cols; j++) {
                if (temparr[i][j] != null)
                    pw.print(temparr[i][j] + ",");
                else
                    pw.print("None,");
            }
            pw.println("");
        }
        pw.close();
        bw.close();
        fw.close();
        System.out.println("Data synchronized with the database!!");
        fileToArray();
    }

    void addBooks() throws IOException {
        System.out.println("Enter the number of books to be added");
        int nob = sc.nextInt();
        String[][] temparr = new String[nob][cols];
        for (int i = 0; i < nob; i++) {
            System.out.println("Enter the title of book " + (i + 1));
            sc.nextLine();
            temparr[i][0] = sc.nextLine();
            System.out.println("Enter the International Standard Book Number of the book " + (i + 1));
            temparr[i][1] = sc.nextLine();
            System.out.println("Enter the author of the book " + (i + 1));
            temparr[i][2] = sc.nextLine();
            System.out.println("Enter the genre of the book " + (i + 1));
            temparr[i][3] = sc.nextLine();
        }
        FileWriter fw = new FileWriter("C:\\Library Software\\File.txt", true);
        arrayToFile(temparr, fw);
        System.out.println("The books have been added to the database");
        System.out.println("Enter any digit to return to the home screen");
        int temp = sc.nextInt();
    }

    void searchBooks() {
        System.out.println("Enter the search filter (1 - Name of the book, 2 - ISBN, 3 - Author, 4 - Genre");
        int searchfilter = sc.nextInt();
        System.out.println("Enter the search parameter");
        sc.nextLine();
        String searchparameter = sc.nextLine();
        int searchcol = 0;
        if (searchfilter > 0 && searchfilter < 5)
            searchcol = searchfilter - 1;
        else {
            System.out.println("Invalid parameters!! Please try again.");
            searchBooks();
        }
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i][searchcol].equalsIgnoreCase(searchparameter)) {
                ++count;
                if (count == 1)
                    System.out.printf("%-2s %-30s  %-15s  %-20s  %-10s  %-15s  %-10s  %-10s", "No", "Title", "ISBN",
                            "Author",
                            "Genre",
                            "Issued to", "Issued on", "Return by");
                System.out.println();
                System.out.printf("%-2d %-30s  %-15s  %-20s  %-10s  %-15s  %-10s  %-10s", (i + 1), arr[i][0], arr[i][1],
                        arr[i][2],
                        arr[i][3], arr[i][4], arr[i][5], arr[i][6]);
                System.out.println();
            }
        }
        if (count == 0)
            System.out.println("The book is not present in the database!!");
        System.out.println("Enter any digit to return to the home screen");
        int temp = sc.nextInt();
    }

    int specialSearch(String task) {
        System.out.println("Enter the name of the book to " + task);
        sc.nextLine();
        String name = sc.nextLine();
        found = 0;
        int i = 0;
        for (; i < arr.length; i++) {
            if (arr[i][0].equalsIgnoreCase(name)) {
                System.out.println("Title: " + arr[i][0]);
                System.out.println("Author: " + arr[i][2]);
                if (task == "return")
                    System.out.println("Borrowed by: " + arr[i][4]);
                System.out.println("Enter 1 to " + task + " this book");
                System.out.println("Enter any other digit to continue searching");
                int choice = sc.nextInt();
                if (choice == 1) {
                    ++found;
                    break;
                }
            }
        }
        return i;
    }

    void issueBooks() throws IOException {
        int i = specialSearch("issue");
        if (found != 0) {
            if (arr[i][4].equalsIgnoreCase("None")) {
                System.out.println("Enter the person to issue the book to:");
                sc.nextLine();
                arr[i][4] = sc.nextLine();
                Date today = Calendar.getInstance().getTime();
                arr[i][5] = new SimpleDateFormat("dd/MM/yyyy").format(today);
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                System.out.println("Enter the number of days for which the book is borrowed");
                int days = sc.nextInt();
                c.add(Calendar.DATE, days);
                arr[i][6] = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
                FileWriter fw = new FileWriter("C:\\Library Software\\File.txt");
                arrayToFile(arr, fw);
            } else
                System.out.println("The book has already been issued to another person!!");
            found = 0;
        } else
            System.out.println("The book is not present in the database!!");
        System.out.println("Enter any digit to return to the home screen");
        int temp = sc.nextInt();
    }

    void returnBooks() throws ParseException, IOException {
        int i = specialSearch("return");
        if (found != 0) {
            if (!(arr[i][4].equalsIgnoreCase("None"))) {
                Date today = Calendar.getInstance().getTime();
                Date returnd = new SimpleDateFormat("dd/MM/yyyy").parse(arr[i][6]);
                long difference_In_Time = today.getTime() - returnd.getTime();
                long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
                if (difference_In_Days > 0) {
                    System.out.println("The book has been returned " + difference_In_Days + " days late");
                    System.out.println("Enter the fine to be paid per day");
                    int fine = sc.nextInt() * (int) difference_In_Days;
                    System.out.println("The fine to be paid is " + fine);
                } else
                    System.out.println("The book has been returned on time");
                System.out.println("Enter 1 to confirm the book's return");
                System.out.println("Enter any other digit to cancel return of the book");
                int choice = sc.nextInt();
                if (choice == 1) {
                    arr[i][4] = "None";
                    arr[i][5] = "None";
                    arr[i][6] = "None";
                    FileWriter fw = new FileWriter("C:\\Library Software\\File.txt");
                    arrayToFile(arr, fw);
                }
            } else
                System.out.println("The book has not been issued yet!! Please issue before returning");
        } else
            System.out.println("The book is not present in the database!");
        System.out.println("Enter any digit to return to the home screen");
        int temp = sc.nextInt();
    }

    void homeScreen() throws IOException, ParseException {
        File f = new File("C:\\Library Software\\File.txt");
        if (f.exists())
            fileToArray();
        else {
            System.out.println("The database is empty!! Begin by adding books");
            addBooks();
        }
        if (f.length() == 0) {
            System.out.println("The database is empty!! Begin by adding books");
            addBooks();
        }
        for (;;) {
            System.out.print("\033[H\033[2J");
            System.out.println("Welcome to the home screen");
            System.out.println("Enter 1 to issue a book");
            System.out.println("Enter 2 to return a book");
            System.out.println("Enter 3 to search for a particular book");
            System.out.println("Enter 4 to add books to the database");
            System.out.println("Enter 5 to view all the books in the database");
            System.out.println("Enter 6 to exit the program");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    issueBooks();
                    break;
                case 2:
                    returnBooks();
                    break;
                case 3:
                    searchBooks();
                    break;
                case 4:
                    addBooks();
                    break;
                case 5:
                    System.out.printf("%-6s %-30s  %-15s  %-20s  %-10s  %-15s  %-10s  %-10s", "Sl No.", "Title", "ISBN",
                            "Author",
                            "Genre",
                            "Issued to", "Issued on", "Return by");
                    System.out.println();
                    for (int i = 0; i < arr.length; i++) {
                        System.out.printf("%-6d %-30s  %-15s  %-20s  %-10s  %-15s  %-10s  %-10s", (i + 1), arr[i][0],
                                arr[i][1],
                                arr[i][2],
                                arr[i][3], arr[i][4], arr[i][5], arr[i][6]);
                        System.out.println();
                    }
                    System.out.println("Enter any digit to return to the home screen");
                    int temp = sc.nextInt();
                    break;
                case 6:
                    sc.nextLine();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid Choice!!");
                    System.out.println("Enter any digit to return to the home screen");
                    int temp2 = sc.nextInt();
            }
        }
    }

    public static void main(String[] args) {
        Library obj = new Library();
        try {
            obj.homeScreen();
        } catch (Exception e) {
            System.out.println("Error Code : " + e);
        }
    }
}