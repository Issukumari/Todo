import java.util.*;
class factors
{
public static void main(String args[])
{
Scanner sc= new Scanner(System.in);
System.out.println("enter no");
int n= sc.nextInt();
int i=2;
System.out.println("Prime factors are");
while(n!=0 && i>0)
{
if(n%i==0)
{
System.out.println(i);
n=n/2;
}
else
i++;
}
}
}
