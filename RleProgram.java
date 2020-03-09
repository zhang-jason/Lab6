import java.util.Scanner;

public class RleProgram
{
    public static void menuDisplay() //prints menu options
    {
        System.out.println("\nRLE Menu");
        System.out.println("--------");
        System.out.println("0. Exit");
        System.out.println("1. Load File");
        System.out.println("2. Load Test Image");
        System.out.println("3. Read RLE String");
        System.out.println("4. Read RLE Hex String");
        System.out.println("5. Read Data Hex String");
        System.out.println("6. Display Image");
        System.out.println("7. Display RLE String");
        System.out.println("8. Display Hex RLE Data");
        System.out.println("9. Display Hex Flat Data\n");
        System.out.print(  "Select a Menu Option: ");
    }

    public static boolean consecutiveFours(int[] arr) //determine whether there are four of the same integer in a row
    {
        boolean ifFour = false;
        int consecutive = 1;

        for(int index = 0; index < arr.length - 1; index++)
        {
            if (arr[index] == arr[index + 1]) //checks if one integer is the same as the one after
            {
                consecutive++; //adds one to the consecutive counter
                if (consecutive == 4) //once there are four of the same integer in a row
                {
                    ifFour = true;
                    break; //exit the program
                }
            }
            else
            {
                consecutive = 1; //if the integers are not the same, reset the counter back to one
            }
        }
        return ifFour;
    }

    public static int[] sumByParity(int[] arr) //finds the sum of integers in the even and odd indices of an array
    {
        int[] res = new int[2]; //create array of two indices (one for even, one for odd)
        int sumEven = 0;
        int sumOdd = 0;

        for(int i = 0; i < arr.length; i++)
        {
            if(i % 2 == 0) //finds all even indices (index is divisible by two, therefore even)
            {
                sumEven += arr[i]; //add value at even indices to the total even sum
            }
            else //finds all odd indices
            {
                sumOdd += arr[i]; //add value at odd indices to the total odd sum
            }
        }

        res[0] = sumEven; //sets the first index of the sum array to that of the even indices
        res[1] = sumOdd; //sets the second index of the sum array to that of the odd indices
        return res;
    }

    public static int[] expandByIndex(int[] arr) //create an expanded array from the values in a previous array
    {
        int size = 0;
        int index = 0;

        for(int i = 0; i < arr.length; i++)
        {
            size += arr[i]; //determines size of expanded array from sum of values in previous array
        }
        int[] res = new int[size];

        for(int i = 0; i < arr.length; i++) //fills values of expanded array based on those from the previous array
        {
            int value = i;
            int times = arr[i];
            for(int j = 0; j < times; j++)
            {
                res[index] = value;
                index++;
            }
        }
        return res;
    }

    public static int numericalCount(String string) //determines the number of numbers (0-9) in a string (not letters)
    {
        int count = 0;

        for(int i = 0; i < string.length(); i++)
        {
            if(string.charAt(i) >= 48 && string.charAt(i) <= 57) //ascii values for numbers 0-9
            {
                count++;
            }
        }
        return count;
    }

    public static String toHexString(byte[] data) //convert data to a hexadecimal string (without delimiters)
    {
        String hexChars[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"}; //array of possible hex values
        String hex = "";

        for(int i = 0; i < data.length; i++)
        {
            hex += hexChars[data[i]]; //adds appropriate hex value to the string based on data in the array
        }
        return hex;
    }

    public static int countRuns(byte[] flatData) //number of runs in a data set
    {
        int count = 1;
        int groups = 1; //number of groups of the same values in the array

        for(int i = 0; i < flatData.length - 1; i++)
        {
            if(flatData[i] == flatData[i + 1]) //checks to see if the value at one index is the same as the one after
            {
                count++; //if so, add to the counter (of consecutive values)
            }
            else
            {
                groups++; //increase the number of groups in the array
            }
            if(count >= 15) //if a group exceeds 15, form a new group
            {
                groups++;
                count = 1;
            }
        }
        return groups;
    }

    public static byte[] encodeRle(byte[] flatData)
    {
        int size = countRuns(flatData); //finds the size of the encoded (RLE) byte array
        byte count = 1;
        byte[] res = new byte[size * 2];
        int i = 0; //represents index of encoded RLE byte array
        int index = 0; //represents index of raw flat data array

        while(i < res.length - 1)
        {
            while(index < flatData.length - 1)
            {
                if(flatData[index] == flatData[index + 1])
                {
                    count++; //adds to consecutive number of a certain value
                    if(count == 15) //if a group exceeds 15 consecutive values, start a new group
                    {
                        res[i] = count; //make the current value the number of consecutive values
                        res[i + 1] = flatData[index]; //make the next value of the array the consecutive value itself
                        count = 1; //reset the number of consecutive values to 1 (for the new group)
                        i+=2; //go to the next empty array index
                        index++; //go to the next index of the flatData array
                    }
                }
                else
                {
                    res[i] = count;
                    res[i + 1] = flatData[index];
                    count = 1;
                    i+=2;
                }
                index++;
            }
            if(index == flatData.length - 1)
            {
                break;
            }
        }
        res[i] = count; //sets the second to last value of the array to be the number of consecutive values (since the while loop is broken and the last two indices will not be addressed
        res[i + 1] = flatData[index];

        return res;
    }

    public static int getDecodedLength(byte[] rleData) //find length of decompressed array
    {
        int[] res = new int[rleData.length];

        for(int i = 0; i < rleData.length; i++) //makes a new int[] array to be able to use sumByParity
        {
            res[i] = rleData[i];
        }

        int sum[] = sumByParity(res);
        return sum[0]; //takes sum of counter values
    }

    public static byte[] decodeRle(byte[] rleData) //returns decoded data set from RLE encoded data
    {
        int size = getDecodedLength(rleData); //find decompressed array length
        int index = 0;
        byte[] res = new byte[size];

        for(int i = 0; i < rleData.length; i+=2)
        {
            byte value = rleData[i + 1]; //takes value to be inserted into array
            int times = rleData[i]; //finds number of times to insert value
            for(int j = 0; j < times; j++)
            {
                res[index] = value; //inserts value into array a certain number of times
                index++;
            }
        }
        return res;
    }

    public static byte[] stringToData(String dataString) //converts a string (in hex) into byte data
    {
        char[] rleHex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'}; //array of possible hex values
        char[] rawHex = {'0', '1', '2', '3', '4', '5' ,'6' ,'7' ,'8' ,'9' ,'a' ,'b' ,'c' ,'d' ,'e' ,'f'};
        byte[] res = new byte[dataString.length()];
        //checks to see if the string is raw/flat data or RLE data
        if(dataString.contains("A") || dataString.contains("B") || dataString.contains("C") || dataString.contains("D") || dataString.contains("E") || dataString.contains("F"))
        {
            for (int i = 0; i < res.length; i++) {
                for (byte index = 0; index < rleHex.length; index++) {
                    char value = dataString.charAt(i);
                    if (value == rleHex[index]) //matches value of the character in the string to one of the values in the hex array
                    {
                        res[i] = index; //adds that value to the array
                    }
                }
            }
        }

        else
        {
            for (int i = 0; i < res.length; i++)
            {
                for (byte index = 0; index < rawHex.length; index++) {
                    char value = dataString.charAt(i);
                    if (value == rawHex[index]) //matches value of the character in the string to one of the values in the hex array
                    {
                        res[i] = index; //adds that value to the array
                    }
                }
            }
        }

        return res;
    }

    public static String toRleString(byte[] rleData)
    {
        String[] hexChars = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        String res = ""; //initialize the resulting string

        for(int i = 0; i < rleData.length; i+=2)
        {
            res = res + rleData[i] + hexChars[rleData[i + 1]] + ":"; //adds the decimal and hexadecimal values to the resulting string
        }
        res = res.substring(0, res.length() - 1); //removes the last colon in the array
        for(int i = 0; i < res.length() - 2; i++)
        {
            if(res.substring(i, i+2).equals("00")) //checks for some random wonkiness I found while testing
            {
                res=res.substring(0, i - 1);
            }
        }
        return res;
    }

    public static byte[] stringToRle(String rleString)
    {
        String hexChars[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        String[] subSec = rleString.split(":"); //divides up the string into an array
        byte[] res = new byte[subSec.length * 2]; //
        int section = 0;

        for(int i = 0; i < res.length; i+=2)
        {
            if(subSec[section].length() == 3) //checks to see if the decimal value in each section is one or two digits
            {
                String temp = subSec[section].substring(0, 2);
                int inter = Integer.parseInt(temp); //convert string into integers
                byte holder = (byte) inter; //convert integers to byte to store in byte array
                res[i] = holder; //inserts converted bytes into resulting array

                temp = subSec[section].substring(2);
                for(byte hex = 0; hex < hexChars.length; hex++)
                {
                    if(temp.toLowerCase().equals(hexChars[hex])) //matches the hexadecimal value to a value in the hexChars array
                    {
                        res[i + 1] = hex;
                    }
                }
            }
            else if(subSec[section].length() == 2) //if decimal is only one digit
            {
                String temp = subSec[section].substring(0, 1);
                for(byte hex = 0; hex < hexChars.length; hex++)
                {
                    if(temp.toLowerCase().equals(hexChars[hex])) //matches decimal value to a 0-9 value in the hexChars array
                    {
                        res[i] = hex;
                    }
                }

                temp = subSec[section].substring(1);
                for(byte hex = 0; hex < hexChars.length; hex++)
                {
                    if(temp.toLowerCase().equals(hexChars[hex])) //matches hexadecimal value to a value in the hexChars array
                    {
                        res[i + 1] = hex;
                    }
                }
            }
            section++;
        }
        return res;
    }

    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to the RLE image encoder!");
        System.out.println("Displaying Spectrum Image:");
        ConsoleGfx.displayImage(ConsoleGfx.testRainbow);
        byte command = 1; //holds input command from user
        byte[] imageData = new byte[0];
        byte[] rleData = new byte[0];

        while(command != 0)
        {
            menuDisplay(); //display RLE menu
            command = input.nextByte();
            if(command == 1)
            {
                System.out.print("Enter name of file to load: ");
                String fileName = input.next(); //take filename from user input
                imageData = ConsoleGfx.loadFile(fileName);
            }

            else if(command == 2)
            {
                imageData = ConsoleGfx.testImage;
                rleData = encodeRle(imageData); //load encoded RLE data from the testImage file
                System.out.println("Test image data loaded.");
            }

            else if(command == 3)
            {
                System.out.print("Enter an RLE string to be decoded: ");
                String decode = input.next();
                rleData = stringToRle(decode); //convert user RLE data string into RLE data byte array
                imageData = decodeRle(rleData); //set imageData to the raw data from encoded RLE data
            }

            else if(command == 4)
            {
                System.out.print("Enter the hex string holding RLE data: ");
                String hex = input.next();
                rleData = stringToData(hex); //convert user hexadecimal string into RLE data byte array
                imageData = decodeRle(rleData); //set imageData to the raw data from encoded RLE data
            }

            else if(command == 5)
            {
                System.out.print("Enter the hex string holding flat data: ");
                String flat = input.next();
                imageData = stringToData(flat); //convert user raw hexadecimal string into raw data byte array
                rleData = encodeRle(imageData); //encode raw data into RLE data
            }

            else if(command == 6)
            {
                System.out.println("Displaying image...");
                try
                {
                    ConsoleGfx.displayImage(imageData);
                }
                catch(Exception e) //catch the thrown exception when imageData is not set yet
                {
                    System.out.println("(no data)");
                }
            }

            else if(command == 7)
            {
                System.out.print("RLE representation: ");
                try
                {
                    String output = toRleString(rleData); //convert RLE data into an RLE string
                    System.out.println(output);;
                }
                catch(Exception e)
                {
                    System.out.println("(no data)");
                }
            }

            else if(command == 8)
            {
                System.out.print("RLE hex values: ");
                String output = toHexString(rleData); //convert RLE data into a hexadecimal string
                if(output.equals(""))
                {
                    System.out.println("(no data)");
                }
                else
                {
                    System.out.println(output);
                }
            }

            else if(command == 9)
            {
                System.out.print("Flat hex values: ");
                String output = toHexString(imageData); //convert RLE data into a raw data string
                if(output.equals(""))
                {
                    System.out.println("(no data)");
                }
                else
                {
                    System.out.println(output);
                }
            }

            else if(command < 0 || command > 9) //if user enters a command outside the menu options...
            {
                System.out.println("Error! Invalid input."); //print invalid input
            }
        }
    }
}