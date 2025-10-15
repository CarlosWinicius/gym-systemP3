package br.upe.data;

public enum TipoUsuario {
    COMUM(0),
    ADMIN(1);

    private final int valor;

    TipoUsuario(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public static TipoUsuario fromValor(int valor) {
        for (TipoUsuario tipo : TipoUsuario.values()) {
            if (tipo.getValor() == valor) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Valor de TipoUsuario inválido: " + valor);
    }
}