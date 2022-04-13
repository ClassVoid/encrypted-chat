package com.example.IdentityCheckServer.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ticket")
@AllArgsConstructor
@NoArgsConstructor
public class TicketModel {
    private @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator( name="native", strategy = "native")
    @Column(name = "ticket_id")
    Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "encr_message", nullable = false, length = 1024)
    private String encrMessage;

}
