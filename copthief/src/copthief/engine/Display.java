package copthief.engine;

import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * Display is the class responsible for drawing images, and
 * monitoring user input. All input is parsed and stored in a
 * private vector. There are methods for obtaining such input.
 * This class, and its nested classes do not need to be modified.
 *
 * @see          java.awt.Frame
 * @version      1.0 beta
 * @author       Ian A Mason
 */


public class Display extends Frame
{
    //inner class GridCanvas
    public class GridCanvas extends Canvas
    {
        private int numRows, numCols, blockSize;
        private NamedImage blank;
        private Image offscreenImage;
        private Graphics offscreenG, onscreenG;

        public GridCanvas(int nRows, int nCols, int size)
        {
            setBackground(Color.white);
            setFont(new Font("SansSerif", Font.PLAIN, 8));
            blockSize = size;
            configureForSize(nRows, nCols);
            blank = NamedImage.findImageNamed("Empty");
        }

        private void checkImage()
        {
            if (offscreenImage == null)
            {
                Dimension sz = getSize();
                offscreenImage = createImage(sz.width, sz.height);
                offscreenG = offscreenImage.getGraphics();
                onscreenG = getGraphics();
            }
        }

        public void configureForSize(int nRows, int nCols)
        {
            numRows = nRows;
            numCols = nCols;
            setSize(blockSize*numCols, blockSize*numRows);

            // Just big enough the squares themselves
            if (offscreenG != null)
            {
                offscreenG.dispose();
            }

            // Throw away previous graphics and re-cache
            if (onscreenG != null)
            {
                onscreenG.dispose();
            }

            // Throw away previous graphics and re-cache
            offscreenImage = null;
            offscreenG = onscreenG = null;
        }

        private void drawCenteredString(Graphics g, String s, Rectangle r)
        {
            FontMetrics fm = g.getFontMetrics();
            g.setColor(Color.black);
            g.drawString(s, r.x + (r.width - fm.stringWidth(s)) / 2, r.y + (r.height + fm.getHeight()) / 2);
        }

        public void drawImageAndLetterAtLocation(String imageFileName, char ch, int x, int y)
        {
            // Make sure location is valid

            // Draw image at location
            drawLocation(x, y, NamedImage.findImageNamed(imageFileName), ch);
        }

        private void drawLocation(int x, int y, NamedImage ni, char letter)
        {
            Rectangle r = rectForLocation(y, x);
            checkImage();

            if (ni == null || ni.isBackgroundImage)
            {
                // Draw blank to erase, or behind background
                offscreenG.drawImage(blank.image, r.x, r.y, null);
            }

            if (ni != null)
            {
                if (!offscreenG.drawImage(ni.image, r.x, r.y, null))
                {
                    // Try to draw image

                    // But image not ready or had error drawing
                    offscreenG.drawImage(blank.image, r.x, r.y, null);

                    // Draw background
                    offscreenG.setColor(Color.gray);

                    // Inset a gray box in its place
                    offscreenG.fillRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2);
                }
            }

            if (letter != '\0')
            {
                drawCenteredString(offscreenG, letter + "", r);
            }

            // Repaint(r.x, r.y, r.width, r.height);
            onscreenG.drawImage(offscreenImage, r.x, r.y, r.x + r.width, r.y + r.height, r.x, r.y, r.x + r.width, r.y + r.height, null);
        }

        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(blockSize * numCols, blockSize * numRows);
        }

        @Override
        public void paint(Graphics g)
        {
            if (offscreenImage != null)
            {
                Rectangle r = g.getClipBounds();

                // Copy just sub-rect from cache
                g.drawImage(offscreenImage, r.x, r.y, r.x + r.width, r.y + r.height, r.x, r.y, r.x + r.width, r.y + r.height, null);
            }
        }

        private Rectangle rectForLocation(int row, int col)
        {
            return new Rectangle(col * blockSize, row * blockSize, blockSize, blockSize);
        }

        @Override
        public void update(Graphics g)
        {
            paint(g);
        }
    }

    // static nested class NamedImage
    static class NamedImage
    {
        private static Vector<NamedImage> allImages = new Vector<NamedImage>();
        private static MediaTracker mt;
        private static String things[] = { "Thief", "Cop" };
        private static String squares[] = { "Empty", "Wall", "Goal" };
        static public NamedImage findImageNamed(String name)
        {
            return findImageNamed(name, false);
        }
        static public NamedImage findImageNamed(String name, boolean isBackgroundImage)
        {
            NamedImage key = new NamedImage(name);
            int foundIndex = allImages.indexOf(key);

            // Search cache for this name
            if (foundIndex != -1)
            {
                return allImages.elementAt(foundIndex);
            }
            // Return shared version
            else
            {
                key.image = Toolkit.getDefaultToolkit().getImage("Images" + java.io.File.separator + name + ".gif");

                // Create image from file
                mt.addImage(key.image, 0);

                // Add to Media Tracker
                try
                {
                    mt.waitForID(0);
                }
                catch (InterruptedException ie)
                {
                }

                allImages.addElement(key);

                // Add to list of all images
                key.isBackgroundImage = isBackgroundImage;
                return key;
            }
        }
        static public void preloadImages(Component target)
        {
            mt = new MediaTracker(target);

            for (String thing : things) {
                findImageNamed(thing);
            }

            for (String square : squares) {
                findImageNamed(square, true);
            }
        }

        public String name;

        public Image image;

        public boolean isBackgroundImage;

        private NamedImage(String n)
        {
            name = n;
        }

        @Override
        public boolean equals(Object o)
        {
            return ((o instanceof NamedImage) && name.equals(((NamedImage)o).name));
        }
    }
    private static final int Margin = 10;
    private static final int FontSize = 10;
    private static final String FontName = "Helvetica";
    private static final int BlockSize = 30;
    private GridCanvas gridCanvas;
    private Vector<Constants.Commands> cmds = new Vector<Constants.Commands>();

    private Label msgField;

    public Display(String title)
    {
        super(title);
        NamedImage.preloadImages(this);
        configureWindow(0, 0);
    }


    private void centerOnScreen()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getSize();
        setLocation((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) / 2);
    }


    public void configureForSize(int numRows, int numCols)
    {
        gridCanvas.configureForSize(numRows, numCols);
        setResizable(false);
        pack();
        centerOnScreen();
    }

    private void configureWindow(int numRows, int numCols)
    {
        setLayout(new BorderLayout(Margin, Margin));
        setBackground(Color.lightGray);
        gridCanvas = new GridCanvas(numRows, numCols, BlockSize);
        add("Center", gridCanvas);
        Panel bp = new Panel();
        bp.setFont(new Font(FontName, Font.PLAIN, FontSize));

        // numRows, numCols, hGap, vGap
        bp.setLayout(new GridLayout(1, 1, 5, 0));
//        bp.add(new Label("Move with the <b>arrow keys</b>, and <b>U</b> for undo.", Label.CENTER));
//        bp.add(new Label("To move to a square, where there is a clear path, just click the mouse.", Label.CENTER));
//        bp.add(new Label("Press <b>N</b> to skip this level, <b>Q</b> to quit, and <b>R</b> to restart this level.", Label.CENTER));
//        bp.add(msgField = new Label("New game", Label.CENTER));
//        msgField.setFont(new Font (FontName, Font.BOLD, FontSize + 2));
        add("South", bp);
        pack();
        addWindowListener
                (
                        new WindowAdapter()
                        {
                            @Override
                            public void windowClosing(WindowEvent e)
                            {
                                System.exit(0);
                            }
                        }
                );
        gridCanvas.addKeyListener
                (
                        new KeyAdapter()
                        {
                            @Override
                            public void keyPressed(KeyEvent ke)
                            {
                                Display.this.addCommand(ke);
                            }
                        }
                );

    }

    public synchronized void addCommand(KeyEvent ke)
    {
        Constants.Commands cmd = Constants.Commands.fromKeyCode(ke.getKeyCode());
        cmds.addElement(cmd);

        // Rendezvous with anyone waiting
        notify();
    }


    public void doDrawStatusMessage(String msg)
    {
        msgField.setText(msg);
    }

    public void drawAtLocation(String name, char ch, int x, int y)
    {
        gridCanvas.drawImageAndLetterAtLocation(name, ch, x, y);
    }

    public void drawAtLocation(String name, int x, int y)
    {
        if(name.equals("Cop") || name.equals("Thief")) {
            drawAtLocation("Empty", '\0', x, y);
        }
        drawAtLocation(name, '\0', x, y);
    }

    public void drawStatusMessage(String msg)
    {
        doDrawStatusMessage(msg);
    }

    public boolean grabFocus()
    {
        gridCanvas.requestFocus();
        return gridCanvas.hasFocus();
    }

    public synchronized Constants.Commands getCommandFromUser()
    {
        while (cmds.size() == 0)
        {
            // while vector of commands is empty
            try
            {
                wait();
            }
            catch (InterruptedException e)
            {
            }
            // wait for notify
        }

        Constants.Commands cmd = cmds.elementAt(0);

        // Pull first command out of queue
        cmds.removeElementAt(0);

        return cmd;
    }
}