
import java.util.*;
import java.lang.*;
 public class Toss
{
double value;
int toss,h,t;
	Toss(int toss)
	{
	this.toss=toss;
	}
	public void getvalue(double value)
	{
	this.value=value;
	if(value>=0 && value<=0.500)
	h++;
	else
	t++;
	}
	public void display()
	{
	double head_per=100*(h/toss);
	System.out.println("head percent"+" " +head_per);
	double tail_per=100*(t/toss);
	System.out.println("tail percent"+" " +tail_per);
	} 
}
public static void main(String args[])
{
int i,toss;
double value;
Scanner sc= new Scanner(System.in);
System.out.println("Enter no of toss");
toss=sc.nextInt();
Toss ts=new Toss(toss);
	for(i=0;i<toss;i++)
	{
	value=Math.random();
	ts.getvalue(value);
	}
}

