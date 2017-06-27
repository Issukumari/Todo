import java.util.*;
class harmonic
{
public static void main(String args[])
{
Scanner sc=new Scanner(System.in);
System.out.println("enter terms");
int n= sc.nextInt();
int i=1;
float sum=0;
for(int j=0;j<=n;j++)
{
sum=sum+(float)1/i;
i++;
}
System.out.println("sum="+sum);
}
}
