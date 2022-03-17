package com.grp6.edim.server;

import com.grp6.edim.server.logging.LogLevel;
import com.grp6.edim.server.logging.Logger;
import com.grp6.edim.shared.Activity;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class ActivityManager {
    private ArrayList<Activity> activityList;

    public Activity getRandomActivity(){
        Random random = new Random();
        return activityList.get(random.nextInt(activityList.size()));
    }

    public Activity getActivity(int index){
        return activityList.get(index);
    }

    public ArrayList<Activity> getActivities(){
        return activityList;
    }


    public ActivityManager(String file) {
        this.activityList = buildRegister(file);
    }

    public ArrayList<Activity> buildRegister(String file) {
        ArrayList<Activity> activityList = new ArrayList<Activity>();

        try  {
            try (BufferedReader reader = new BufferedReader(new FileReader("files/activities.dat"))) {
                String current = null;
                Activity activity = null;

                while ((current = reader.readLine()) != null) {
                    String[] split = current.split(":");
                    switch (split[0]) {
                        case "title" -> {
                            if (activity == null){
                                activity = new Activity(split[1]);
                            }
                        }
                        case "instruction" -> {
                            if (activity != null){
                                activity.setInstruction(split[1]);
                            }
                        }
                        case "description" -> {
                            if (activity != null){
                                activity.setDescription(split[1]);
                            }
                        }
                        case "image_path" -> {
                            if (activity != null){
                                activity.setImage(new ImageIcon(split[1]));
                                activityList.add(activity);
                                activity = null;
                            }
                        }
                        default -> Logger.log("Invalid option in switch case : " + split[0], LogLevel.Warning);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return activityList;
    }


    public void saveActivity(Activity data) {

        if (data.getName() == null) {
            return;
        }
        if (data.getInstruction() == null) {
            return;
        }
        if (data.getInfo() == null) {
            return;
        }
        if (data.getImage() == null) {
            return;
        }

        try  {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("files/activities.dat"))) {

                for (Activity activity: activityList) {
                    writer.write("title:" + activity.getName());
                    writer.write("instruction:" + activity.getInstruction());
                    writer.write("description:" + activity.getInfo());
                    String imagePath = "images_server/" + activity.getName() + ".jpg";
                }


                writer.write("title:" + data.getName());
                writer.write("instruction:" + data.getInstruction());
                writer.write("description:" + data.getInfo());

                ImageIcon icon = data.getImage();
                String imagePath = "images_server/" + data.getName() + ".jpg";
                BufferedImage image = (BufferedImage) icon.getImage();

                try {
                    if(image != null) {
                        File file = new File(imagePath);
                        ImageIO.write(image, "jpg", file);
                    }

                }catch(Exception e) {    }

                writer.write("image_path:" + imagePath);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
