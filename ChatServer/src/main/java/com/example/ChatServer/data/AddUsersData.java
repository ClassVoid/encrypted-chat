package com.example.ChatServer.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUsersData {
    List<AddUserData> addUserDataList;
    private UserEncrMessage userEncrMessage;
}
