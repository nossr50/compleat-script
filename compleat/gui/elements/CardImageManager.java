package compleat.gui.elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import io.magicthegathering.javasdk.resource.Card;

public class CardImageManager {
	
	private Color rectColor = null;
	private Image cardArt = null;
	private GC gc = null;
	
	private String defaultImage = "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=439390&type=card";
	private Rectangle[] rectangles = null;
	private Composite cardArtCanvas;
	
	public CardImageManager(Composite canvas, Display display)
	{
		cardArtCanvas = canvas;
		InitWidget(display);
		AddListener(canvas);
	}
	
	private void InitWidget(Display display)
	{
        loadDefaultImage(display); //This might be problematic
	}
	
	public Image getImage()
	{
		return cardArt;
	}
	
	synchronized public void loadImage(Display display, Card card) {
		try {
			
        	URL cardURL = new URL(card.getImageUrl());
        	//System.out.println("Loading new art: "+card.getImageUrl());
        	//Load card art
            cardArt = new Image(display, cardURL.openStream());
            
            Rectangle cardRect = cardArt.getBounds();
            System.out.println("Card Art: " + cardRect.height + " "+ cardRect.width);
            
            gc = new GC(cardArt);
            gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
            
            int width = cardArt.getBounds().width;
            int height = cardArt.getBounds().height;
            
            System.out.println("W/H: "+width+" "+height);
            
            //gc.drawImage(cardArt,0,0,width,height,0,0,width*2,height*2);
            
            cardArtCanvas.redraw();
            cardArtCanvas.update();
            
            gc.dispose();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
	
	public void loadDefaultImage(Display display) {
        try {
        	URL cardURL = new URL(defaultImage);
        	//Load card art
            cardArt = new Image(display, cardURL.openStream());
            
            Rectangle cardRect = cardArt.getBounds();
            System.out.println("Card Art: " + cardRect.height + " "+ cardRect.width);
            
            GC gc = new GC(cardArt);
            //gc.drawRectangle(getBounds());
            gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
            gc.dispose();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
     // on each paint event (first view, window resize, redraw method, ...) make my code
        
    }
	
	public Rectangle getBounds()
	{
		return cardArt.getBounds();
	}
	
	public void AddListener(Composite composite)
	{
		System.out.println("Painter listener added!");
		composite.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
            	//System.out.println("Draw!");
                // draw the image from (0,0) in it's own size
                e.gc.drawImage(cardArt, 0, 0, cardArt.getBounds().width, cardArt.getBounds().height, 0, 0, cardArt.getBounds().width, cardArt.getBounds().height);

                // set foreground color (paint color)
                //e.gc.setForeground(rectColor);
                
                // draw all loaded rectangles
                //for(int i = 0; i < rectangles.length; i++) e.gc.drawRectangle(rectangles[i]);
            }
        });
		
		System.out.println("Painter listener added!");
	}
}
