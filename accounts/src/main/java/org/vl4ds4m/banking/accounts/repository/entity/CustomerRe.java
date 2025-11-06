package org.vl4ds4m.banking.accounts.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

import static org.vl4ds4m.banking.accounts.repository.entity.CustomerRe.ColumnName.*;
import static org.vl4ds4m.banking.accounts.repository.entity.CustomerRe.TABLE_NAME;

@Entity
@Table(name = TABLE_NAME,
    uniqueConstraints = {
        @UniqueConstraint(columnNames = NAME,
            name = TABLE_NAME + "_natkey")})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CustomerRe {

    static final String TABLE_NAME = "customers";

    @Id
    @GeneratedValue
    @Column(name = ID)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = NAME,
            nullable = false)
    private String name;

    @Column(name = FIRST_NAME,
            nullable = false)
    private String firstName;

    @Column(name = LAST_NAME,
            nullable = false)
    private String lastName;

    @Column(name = BIRTH_DATE,
            nullable = false)
    private LocalDate birthDate;

    @OneToMany(mappedBy = AccountRe_.CUSTOMER)
    private Set<AccountRe> accounts;

    static class ColumnName {

        static final String ID = "id";

        static final String NAME = "name";

        static final String FIRST_NAME = "first_name";

        static final String LAST_NAME = "last_name";

        static final String BIRTH_DATE = "birth_date";
    }
}
