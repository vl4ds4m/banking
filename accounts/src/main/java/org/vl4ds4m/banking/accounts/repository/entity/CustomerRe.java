package org.vl4ds4m.banking.accounts.repository.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

import static org.vl4ds4m.banking.accounts.repository.entity.CustomerRe.ColumnName.*;
import static org.vl4ds4m.banking.accounts.repository.entity.CustomerRe.TABLE_NAME;

@Entity
@Table(name = TABLE_NAME,
    uniqueConstraints = @UniqueConstraint(columnNames = LOGIN))
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CustomerRe {

    static final String TABLE_NAME = "customers";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = LOGIN,
            nullable = false)
    private String login;

    @Column(name = FORENAME,
            nullable = false)
    private String forename;

    @Column(name = SURNAME,
            nullable = false)
    private String surname;

    @Column(name = BIRTHDATE,
            nullable = false)
    private LocalDate birthdate;

    @OneToMany(mappedBy = AccountRe_.CUSTOMER)
    private Set<AccountRe> accounts;

    static class ColumnName {

        static final String ID = "id";

        static final String LOGIN = "login";

        static final String FORENAME = "forename";

        static final String SURNAME = "surname";

        static final String BIRTHDATE = "birthdate";

    }

}
