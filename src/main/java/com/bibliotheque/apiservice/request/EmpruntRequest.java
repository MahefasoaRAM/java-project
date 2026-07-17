package com.bibliotheque.apiservice.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EmpruntRequest {
  private Long livreId;

  private LocalDate dateRetourPrevue;
}
