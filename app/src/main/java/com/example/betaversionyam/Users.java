package com.example.betaversionyam;

/**
 * @author		Yam Azoulay
 * @version	    1.0
 * @since		13/02/2020
 *
 * this java class allows to create a new Item named Users. Used to save all the required details of the users of the application.
 */

public class Users {

        private String name, email, phone, uid;
        private boolean isWorker;

        public Users (){}

        public Users (String name, String email, String phone, String uid, boolean isWorker) {
            this.name=name;
            this.email=email;
            this.phone=phone;
            this.uid = uid;
            this.isWorker = isWorker;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name=name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email=email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) { this.phone=phone; }

        public boolean getIsWorker(){ return isWorker;}

        public void setIsWorker(){ this.isWorker = isWorker;}

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
}

