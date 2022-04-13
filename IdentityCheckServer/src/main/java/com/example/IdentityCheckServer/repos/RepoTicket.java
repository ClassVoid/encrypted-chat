package com.example.IdentityCheckServer.repos;

import com.example.IdentityCheckServer.models.TicketModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepoTicket extends JpaRepository<TicketModel, Long> {
    Optional<List<TicketModel>> findTicketModelByUsername(String username);

    void deleteByEncrMessage(String encrMsg);
}
