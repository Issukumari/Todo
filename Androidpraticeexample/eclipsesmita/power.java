import java.util.*;
class power
{
public static void main(String args[])
{
Scanner sc= new Scanner(System.in);
System.out.println("Enter n ");
int n= sc.nextInt();
int i=0;
int p=1;
for(int j=1;j<=n;j++)
{
System.out.println("2 ^"+i+"="+p);
p=p*2;
i++;
}
}
}
