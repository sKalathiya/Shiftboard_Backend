package com.Me.ShiftBoard.Util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {

       private String street;
        private String city;
        private String state;
        private String country;
        private  String zipCode;

    public boolean checkAnyNull() {

        if(
                this.city != null &&
                        this.street != null &&
                        this.state != null &&
                        this.country != null &&
                        this.zipCode != null
        )return false;
        return true;
    }
}
