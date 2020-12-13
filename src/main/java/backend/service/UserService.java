package backend.service;


import backend.model.request_bodies.UserRequestBody;

public interface UserService {
    UserRequestBody getByEmail(String email);
    UserRequestBody changeData(UserRequestBody userRequestBody, String data);
    UserRequestBody addUser(UserRequestBody userRequestBody);
}