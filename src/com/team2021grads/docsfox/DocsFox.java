package com.team2021grads.docsfox;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.awt.*;
import javafx.scene.control.Button;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import java.nio.file.Paths;
import java.nio.file.Files;

public class DocsFox extends Application
{
    double starting_point_x, starting_point_y ;
    double startimagex,startimagey;

    Group rectangle = new Group() ;

    Rectangle new_rectangle = null ;

    boolean new_rectangle_is_being_drawn = false ;

    Color[] rectangle_colors = { Color.TRANSPARENT };
    private java.awt.event.KeyEvent KeyEvent;

    //  The following method adjusts coordinates so that the rectangle
    //  is shown "in a correct way" in relation to the mouse movement.

    void adjust_rectangle_properties( double starting_point_x,
                                      double starting_point_y,
                                      double ending_point_x,
                                      double ending_point_y,
                                      Rectangle given_rectangle )
    {
        given_rectangle.setX( starting_point_x ) ;
        given_rectangle.setY( starting_point_y ) ;
        given_rectangle.setWidth( ending_point_x - starting_point_x ) ;
        given_rectangle.setHeight( ending_point_y - starting_point_y ) ;

        if ( given_rectangle.getWidth() < 0 )
        {
            given_rectangle.setWidth( - given_rectangle.getWidth() ) ;
            given_rectangle.setX( given_rectangle.getX() - given_rectangle.getWidth() ) ;
        }

        if ( given_rectangle.getHeight() < 0 )
        {
            given_rectangle.setHeight( - given_rectangle.getHeight() ) ;
            given_rectangle.setY( given_rectangle.getY() - given_rectangle.getHeight() ) ;
        }
    }

    Rectangle2D testRect = new Rectangle2D.Double();

    public void start( Stage stage ) throws IOException {
        PDFTextStripperByArea PDArea = new PDFTextStripperByArea();
        final int[] cPage = {0};

        //Opens a file chooser and accepts a pdf file from the user
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open PDF File");
        PDDocument doc = PDDocument.load(fileChooser.showOpenDialog(stage));

        //Makes an image from the accepted pdf
        PDFRenderer renderer = new PDFRenderer(doc);
        BufferedImage[] Pages = new BufferedImage[doc.getNumberOfPages()];
        for(int i = 0; i<Pages.length;i++)
        {
            Pages[i] = renderer.renderImage(i);
        }
        Image image = SwingFXUtils.toFXImage(Pages[0], null );
        ImageView imageView = new ImageView(image);

        //Creates a number of buttons and appends them to our Horizontal Box
        Button extract = new Button("Extract");
        Button next_page = new Button("Next page");
        Button export = new Button("Export");
        Button edit = new Button("Edit CSV");
        HBox hbox = new HBox();
        hbox.getChildren().add(extract);
        hbox.getChildren().add(next_page);
        hbox.getChildren().add(export);
        hbox.getChildren().add(edit);
        final String[] csvLoc = {""};

        //2D Arraylist of Strings for the storage of data later on
        final List<List<String>> data = new ArrayList<List<String>>();

        //Button for extracting data from the pdf
        extract.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                //Extracts data from specified rectangular region
                PDArea.addRegion("Test",testRect);
                try {
                    PDArea.extractRegions(doc.getPage(cPage[0]));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Adds data to a 2D arraylist of Strings
                final List<String> x = new ArrayList<String>();
                String[] split = (PDArea.getTextForRegion("Test")).split("\\n");
                for(String newLine : split)
                {
                    x.add(newLine);
                }
                data.add(x);
            }
        });

        //Button for scrolling between pages
        next_page.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if(cPage[0]+1 >= Pages.length)
                {
                    cPage[0] = 0;
                }
                else
                {
                    cPage[0] = cPage[0]+1;
                }
                imageView.setImage(SwingFXUtils.toFXImage(Pages[cPage[0]], null )); //Displays current page
            }
        });

        //Button for exporting data as a csv file
        export.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    StringBuilder sb = new StringBuilder();
                    String current;

                    //parses through 2D arraylist of Data
                    for(int i = 0; i < getMax(data); i++)
                    {
                        for(int j = 0; j < data.size(); j++)
                        {
                            if(i<data.get(j).size())
                            {
                                //gets current cell of data and appends it to the csv
                                current = data.get(j).get(i);
                                current = current.replace("\n", "");
                                current = current.replace("\r", "");
                                current = current.replace("\r\n", "");
                                sb.append(current);
                                sb.append(",");
                            }
                        }
                        sb.append("\n");
                    }

                    //Saves CSV to a selected file
                    fileChooser.setTitle("Save CSV");
                    Files.write(Paths.get(csvLoc[0] = fileChooser.showSaveDialog(stage).getPath()), sb.toString().getBytes());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        //Button for opening the csv file so that it can be edited
        edit.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    File files = new File(csvLoc[0]);
                    Desktop desktop = Desktop.getDesktop();
                    if(files.exists())         //checks file exists or not
                        desktop.open(files);              //opens the specified file

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //Setting the position of the image
        imageView.setX(50);
        imageView.setY(25);

        //Setting the preserve ratio of the image view
        imageView.setPreserveRatio(true);

        //Creating a Group object
        Group root = new Group(imageView, rectangle,hbox);

        //Creating a scene object
        Scene scene = new Scene(root, 600, 500);
        scene.setFill(Color.BEIGE);

        //Setting title to the Stage
        stage.setTitle("DocsFox");
        stage.setMaximized(true);


        //-------------------------------------Rectangle----------------------------------------------
        scene.setOnMousePressed( ( MouseEvent event ) ->
        {
            if ( new_rectangle_is_being_drawn == false )
            {
                starting_point_x = event.getSceneX() ;
                starting_point_y = event.getSceneY() ;

                new_rectangle = new Rectangle() ;

                // A non-finished rectangle has always the same color.
                new_rectangle.setFill( Color.TRANSPARENT ) ; // Color when it's being drawn
                new_rectangle.setStroke( Color.BLACK ) ;

                rectangle.getChildren().add( new_rectangle ) ;

                new_rectangle_is_being_drawn = true ;
            }

            // I added this to make the old rectangle disappear and allow to make a new one
            if (new_rectangle_is_being_drawn = true )
            {
                rectangle.getChildren().remove( new_rectangle ) ;
                starting_point_x = event.getSceneX() ;
                starting_point_y = event.getSceneY() ;

                new_rectangle = new Rectangle() ;

                // A non-finished rectangle has always the same color.
                new_rectangle.setFill( Color.TRANSPARENT ) ; // Color when it's being drawn
                new_rectangle.setStroke( Color.BLACK ) ;

                rectangle.getChildren().add( new_rectangle ) ;
            }
        } ) ;

        scene.setOnMouseDragged( ( MouseEvent event ) ->
        {
            if ( new_rectangle_is_being_drawn == true )
            {
                double current_ending_point_x = event.getSceneX() ;
                double current_ending_point_y = event.getSceneY() ;


                adjust_rectangle_properties( starting_point_x,
                        starting_point_y,
                        current_ending_point_x,
                        current_ending_point_y,
                        new_rectangle ) ;
            }
        } ) ;

        scene.setOnMousePressed( ( MouseEvent event ) ->
        {
            if ( new_rectangle_is_being_drawn == false )
            {
                starting_point_x = event.getSceneX() ;
                starting_point_y = event.getSceneY() ;



                new_rectangle = new Rectangle() ;

                // A non-finished rectangle has always the same color.
                new_rectangle.setFill( Color.TRANSPARENT ) ; // Color when it's being drawn
                new_rectangle.setStroke( Color.BLACK ) ;

                rectangle.getChildren().add( new_rectangle ) ;

                new_rectangle_is_being_drawn = true ;
            }

            // I added this to make the old rectangle disappear and allow to make a new one
            if (new_rectangle_is_being_drawn = true )
            {
                rectangle.getChildren().remove( new_rectangle ) ;
                starting_point_x = event.getSceneX() ;
                starting_point_y = event.getSceneY() ;

                new_rectangle = new Rectangle() ;

                // A non-finished rectangle has always the same color.
                new_rectangle.setFill( Color.TRANSPARENT ) ; // Color when it's being drawn
                new_rectangle.setStroke( Color.BLACK ) ;

                rectangle.getChildren().add( new_rectangle ) ;
            }
        } ) ;

        scene.setOnMouseDragged( ( MouseEvent event ) ->
        {
            if ( new_rectangle_is_being_drawn == true )
            {
                double current_ending_point_x = event.getSceneX() ;
                double current_ending_point_y = event.getSceneY() ;


                adjust_rectangle_properties( starting_point_x,
                        starting_point_y,
                        current_ending_point_x,
                        current_ending_point_y,
                        new_rectangle ) ;
            }
        } ) ;


        imageView.setPickOnBounds(true);
        imageView.setOnMousePressed(e ->
        {
            startimagex = e.getX()-50;
            startimagey = e.getY()-25;
        });
        imageView.setOnMouseDragged(e ->
        {
            testRect.setFrameFromDiagonal(startimagex,startimagey,e.getX()-50,e.getY()-25);
        });

        stage.setScene(scene);
        stage.show();
    }

    int getMax(List<List<String>> data)
    {
        int max = 0;
        for(int i = 0; i < data.size(); i++)
        {
            if(data.get(i).size() > max)
            {
                max = data.get(i).size();
            }
        }
        return max;
    }

    public static void main( String[] command_line_parameters )
    {
        launch( command_line_parameters ) ;
    }
}
