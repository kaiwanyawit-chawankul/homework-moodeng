// Online C# Editor for free
// Write, Edit and Run your C# code using C# Online Compiler

using System;

public class HelloWorld
{
    public static void Main(string[] args)
    {
        Random r = new Random();
        for(var i=0;i<100;i++){
            var x = r.Next(1024);
            var y = r.Next(768);
            var z = r.Next(230);
            Console.WriteLine($"INSERT INTO heatmap (x, y, count) VALUES ({x}, {y}, {z});");
        }
        Console.WriteLine ("Try programiz.pro");
    }
}