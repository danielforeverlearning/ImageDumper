import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import javax.imageio.ImageIO;


public class ImageDumper
{
	
	public static Boolean WriteToFile(byte[] bytearray, Boolean dumpcolorandalpha)
    {
        try {
			 //150w x 165h == 24750 pixels
			 //24750 pixels
			 //99000 bytes output  == 4 bytes per pixel, 3 RGB and 1 alpha == bytearray.length
			 //74250 bytes output  == 3 bytes per pixel, RGB only
			 //148500 bytes output == ASCII representation, 2 chars per byte == 2 x 74250
			 
			 int maxpixelsperstring = 660;  //99000 / 150 output strings maximum ==  660
			 int stringnum = 1;
			 
			 String outputstring = String.format("/*%03d*/ myimageobj.AddColorByteString(\"",  stringnum);
			 
			 int dumped = 0;
			 
             FileWriter mywriter = new FileWriter("output.txt");
			 for (int ii=0; ii < bytearray.length; ii++)
			 {
				 if ((ii%maxpixelsperstring) == 0)
				 {
					 if (ii > 0)
					 {
					     outputstring += "\");";
					     
						 mywriter.write(outputstring);
					     mywriter.write("\n");
					     stringnum++;
					     
						 outputstring = String.format("/*%03d*/ myimageobj.AddColorByteString(\"",  stringnum);
					 }
					 else
						 mywriter.write("\n");
				 }
					 
				 if (dumpcolorandalpha)
				 {
					 outputstring += String.format("%02X", bytearray[ii]);
					 dumped++;
				 }
				 else if ((ii%4) != 0) { //dump color only no alpha
					 outputstring += String.format("%02X", bytearray[ii]);
					 dumped++;
				 }
			 }
			 //dump last string
			 outputstring += "\");";
			 mywriter.write(outputstring);
			 mywriter.write("\n");
			 
			 mywriter.write("\n");
             mywriter.close();
			 System.out.print(String.format("dumped=%d\n", dumped));
        }
        catch (IOException ex) {
             System.out.println("WriteToFile: IOException caught");
             ex.printStackTrace();
             return false;
        }
        return true;
    }
	
    public static byte[] GetByteArray(String ImageName)
    {
        try
		{
			File imgPath = new File(ImageName);
			BufferedImage bufferedImage = ImageIO.read(imgPath);

			// get DataBufferBytes from Raster
			WritableRaster raster = bufferedImage .getRaster();
			DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();
			return ( data.getData() );
		}
		catch (IOException ex)
		{
			System.out.println(": IOException caught");
            ex.printStackTrace();
			return null;
		}
    }


    public static void main(String[] args)
    {
        // open image
		System.out.println(String.format("args.length == %d\n", args.length));
		if (args.length < 2)
			System.out.println("Usage: java.exe -cp . ImageDumper <imagefile-path-filename> <true=dumpcolorandalpha false=dumpcoloronly>");
		else
		{
			String ImageName = args[0];
			
			System.out.println(String.format("args[1] == %s\n", args[1])); 
			Boolean dumpcolorandalpha = true;
			String tempstr = args[1].toLowerCase().trim();
			if (tempstr.equals("false"))
				dumpcolorandalpha = false;
			
		    System.out.println("ImageName=" + ImageName);
			if (dumpcolorandalpha)
				System.out.println("DUMPING COLOR AND ALPHA");
			else
				System.out.println("DUMPING COLOR ONLY");
			
		byte[] mybytearray = GetByteArray(ImageName);
		WriteToFile(mybytearray, dumpcolorandalpha);
		System.out.println("DONE");
		}
    }

}//class


