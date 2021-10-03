package org.vmorales.junit5app.ejemplos.models;

import org.vmorales.junit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

public class Cuenta {
    private String persona;
    private BigDecimal saldo;
    private Banco banco;

    public Cuenta(String persona, BigDecimal saldo) {
        this.persona = persona;
        this.saldo = saldo;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public void debito( BigDecimal monto)
    {
        BigDecimal nuevoSaldo = this.saldo.subtract( monto );
        if( nuevoSaldo.compareTo( BigDecimal.ZERO ) < 0)
            throw new DineroInsuficienteException( "Dinero insuficiente" );
        this.saldo = nuevoSaldo;
    }

    public void credito( BigDecimal monto)
    {
        this.saldo = this.saldo.add( monto );
    }

    public Banco getBanco()
    {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    @Override
    public boolean equals(Object obj)
    {
        if( !(obj instanceof Cuenta ) )
            return false;
        Cuenta c1 = (Cuenta) obj;
        if( this.persona == null || saldo == null)
           return false;
       return this.persona.equals(c1.getPersona()) && this.saldo.equals(c1.getSaldo());
    }
}
