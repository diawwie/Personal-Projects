import java.util.Random;
import java.util.Scanner;

public class NrGuesser
{
    static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();
        int number = rand.nextInt(100) + 1;
        int guess = 0;

        System.out.println("Guess a nr between 1 and 100: ");

        while (guess != number)
        {
            guess = sc.nextInt();
            if (guess < number)
                System.out.println("Too low! Try again: ");
            else if (guess > number)
                System.out.println("Too high! Try again: ");
            else
                System.out.println("Congrats! You guessed it!");
        }
        sc.close();
    }
}
