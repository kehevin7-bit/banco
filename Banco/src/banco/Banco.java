package banco;

import java.util.*;

// 👤 Clase Cliente
class Cliente {
    private String nombres;
    private String apellidos;
    private int edad;
    private String representante;

    public Cliente(String nombres, String apellidos, int edad, String representante) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.edad = edad;
        this.representante = representante;
    }

    public void mostrarInfo() {
        System.out.println("Cliente: " + nombres + " " + apellidos);
        System.out.println("Edad: " + edad);
        if (edad < 18) {
            System.out.println("Representante: " + representante);
        }
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}

// 💰 Clase general de cuenta
abstract class Cuenta {
    protected Cliente cliente;
    protected double saldo;
    protected String tipo;

    public Cuenta(Cliente cliente, double saldo, String tipo) {
        this.cliente = cliente;
        this.saldo = saldo;
        this.tipo = tipo;
    }

    public abstract void calcularCierreMes();

    public void mostrarInfo() {
        cliente.mostrarInfo();
        System.out.println("Tipo de cuenta: " + tipo);
        System.out.println("Saldo actual: $" + String.format("%,.2f", saldo));
    }

    // ➕ Depósitos
    public void depositar(double monto) {
        if (monto > 0) {
            double comision = 0;

            if (monto < 500000) {
                comision = 7000;
                System.out.println("💰 Depósito menor a $500,000. Comisión fija de $7,000.");
            } else if (monto >= 500000 && monto < 2000000) {
                comision = 5000 + (monto * 0.02);
                System.out.println("💰 Depósito entre $500,000 y $2,000,000.");
                System.out.println("Comisión: $5,000 + 2% del monto depositado.");
            } else if (monto >= 2000000 && monto <= 10000000) {
                comision = 2000 + (monto * 0.005);
                System.out.println("💰 Depósito entre $2,000,000 y $10,000,000.");
                System.out.println("Comisión: $2,000 + 0.5% del monto depositado.");
            } else if (monto > 10000000) {
                comision = monto * 0.033;
                System.out.println("💰 Depósito mayor a $10,000,000. Comisión del 3.3%.");
            }

            saldo += monto - comision;
            System.out.println("💸 Comisión total cobrada: $" + String.format("%,.2f", comision));
            System.out.println("✅ Depósito exitoso. Nuevo saldo: $" + String.format("%,.2f", saldo));
        } else {
            System.out.println("❌ El monto debe ser mayor a 0.");
        }
    }

    // 💸 Retiros desde cajero (propio o externo)
    public void retirar(double monto, boolean cajeroPropio) {
        double comision = cajeroPropio ? 0 : 4500;
        if (monto > 0 && monto + comision <= saldo) {
            saldo -= (monto + comision);
            if (cajeroPropio) {
                System.out.println("💸 Retiro exitoso en cajero del banco. Sin comisión.");
            } else {
                System.out.println("🏧 Retiro en cajero externo. Comisión de $4,500 aplicada.");
            }
            System.out.println("Monto retirado: $" + String.format("%,.2f", monto));
            System.out.println("Nuevo saldo: $" + String.format("%,.2f", saldo));
        } else {
            System.out.println("❌ Fondos insuficientes o monto inválido.");
        }
    }

    // 🔁 Transferencias con confirmación y aviso de comisión
    public void transferir(Cuenta destino, double monto) {
        Scanner sc = new Scanner(System.in);

        if (monto <= 0) {
            System.out.println("❌ El monto debe ser mayor a 0.");
            return;
        }

        double comision = monto * 0.005; // 0.5% del monto transferido
        double total = monto + comision;

        System.out.println("\n📤 Transferencia a: " + destino.getNombreCliente());
        System.out.println("Monto a transferir: $" + String.format("%,.2f", monto));
        System.out.println("💸 Comisión (0.5%): $" + String.format("%,.2f", comision));
        System.out.println("Total que se descontará de tu cuenta: $" + String.format("%,.2f", total));
        System.out.print("¿Desea continuar con la transferencia? (s/n): ");
        String confirmacion = sc.nextLine();

        if (!confirmacion.equalsIgnoreCase("s")) {
            System.out.println("🚫 Transferencia cancelada por el usuario.");
            return;
        }

        if (total > saldo) {
            System.out.println("❌ Fondos insuficientes. Saldo disponible: $" + String.format("%,.2f", saldo));
            return;
        }

        saldo -= total;
        destino.saldo += monto;

        System.out.println("✅ Transferencia exitosa de $" + String.format("%,.2f", monto));
        System.out.println("💸 Comisión cobrada: $" + String.format("%,.2f", comision));
        System.out.println("Nuevo saldo en tu cuenta: $" + String.format("%,.2f", saldo));
    }

    public double getSaldo() {
        return saldo;
    }

    public String getNombreCliente() {
        return cliente.getNombreCompleto();
    }
}

// 💵 Cuenta de Ahorros
class CuentaAhorros extends Cuenta {
    private static final double TASA_RENDIMIENTO_ANUAL = 0.022; // 2.2% anual
    private static final double TASA_MENSUAL = TASA_RENDIMIENTO_ANUAL / 12;

    public CuentaAhorros(Cliente cliente, double saldo) {
        super(cliente, saldo, "Ahorros");
    }

    @Override
    public void calcularCierreMes() {
        double rendimiento = saldo * TASA_MENSUAL;
        saldo += rendimiento;
        System.out.println("🏦 Cierre de mes (Cuenta de Ahorros):");
        System.out.println("Rendimiento ganado: $" + String.format("%,.2f", rendimiento));
        System.out.println("Nuevo saldo: $" + String.format("%,.2f", saldo));
    }
}

// 💳 Cuenta Corriente
class CuentaCorriente extends Cuenta {
    private static final double TASA_MENSUAL = 0.015;
    private static final double COSTO_CHEQUE = 3000;

    public CuentaCorriente(Cliente cliente, double saldo) {
        super(cliente, saldo, "Corriente");
    }

    @Override
    public void calcularCierreMes() {
        double descuento = saldo * TASA_MENSUAL;
        saldo -= descuento;
        System.out.println("🏦 Cierre de mes (Cuenta Corriente):");
        System.out.println("Se descontó una tasa del 1.5% mensual.");
        System.out.println("Monto descontado: $" + String.format("%,.2f", descuento));
        System.out.println("Nuevo saldo: $" + String.format("%,.2f", saldo));
    }

    public void emitirCheque(double monto) {
        double total = monto + COSTO_CHEQUE;
        if (monto > 0 && total <= saldo) {
            saldo -= total;
            System.out.println("🧾 Cheque emitido por $" + String.format("%,.2f", monto));
            System.out.println("Comisión de cheque: $" + String.format("%,.2f", COSTO_CHEQUE));
            System.out.println("Nuevo saldo: $" + String.format("%,.2f", saldo));
        } else {
            System.out.println("❌ No se puede emitir el cheque (fondos insuficientes).");
        }
    }
}

// 🏦 Clase principal Banco
public class Banco {
    private static Scanner sc = new Scanner(System.in);
    private static List<Cuenta> cuentas = new ArrayList<>();

    public static void main(String[] args) {
        int opcion;
        do {
            System.out.println("\n=== 🏦 MENÚ PRINCIPAL BANCO ===");
            System.out.println("1. Apertura de Cuentas");
            System.out.println("2. Transferencias");
            System.out.println("3. Cajero Automático");
            System.out.println("4. Cierre de mes (Estado de Cuenta)");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> aperturaCuenta();
                case 2 -> transferencias();
                case 3 -> cajeroAutomatico();
                case 4 -> cierreMes();
                case 5 -> System.out.println("👋 Gracias por usar el sistema bancario.");
                default -> System.out.println("❌ Opción inválida.");
            }
        } while (opcion != 5);
    }

    // 1️⃣ Apertura de cuentas
    private static void aperturaCuenta() {
        System.out.print("Ingrese nombres: ");
        String nombres = sc.nextLine();
        System.out.print("Ingrese apellidos: ");
        String apellidos = sc.nextLine();
        System.out.print("Ingrese edad: ");
        int edad = sc.nextInt();
        sc.nextLine();
        String representante = "";
        if (edad < 18) {
            System.out.print("Ingrese nombre del representante: ");
            representante = sc.nextLine();
        }

        System.out.print("Tipo de cuenta (ahorros/corriente): ");
        String tipoCuenta = sc.nextLine().toLowerCase();
        System.out.print("Monto de apertura: ");
        double monto = sc.nextDouble();

        Cuenta cuenta = switch (tipoCuenta) {
            case "ahorros" -> new CuentaAhorros(new Cliente(nombres, apellidos, edad, representante), monto);
            case "corriente" -> {
                if (monto < 200000) {
                    System.out.println("❌ Monto mínimo para cuenta corriente: $200,000.");
                    yield null;
                }
                yield new CuentaCorriente(new Cliente(nombres, apellidos, edad, representante), monto);
            }
            default -> {
                System.out.println("❌ Tipo de cuenta no válido.");
                yield null;
            }
        };

        if (cuenta != null) {
            cuentas.add(cuenta);
            System.out.println("✅ Cuenta creada exitosamente:");
            cuenta.mostrarInfo();
        }
    }

    // 2️⃣ Transferencias
    private static void transferencias() {
        if (cuentas.size() < 2) {
            System.out.println("⚠️ Debe haber al menos dos cuentas registradas.");
            return;
        }

        System.out.println("\nCuentas disponibles:");
        for (int i = 0; i < cuentas.size(); i++) {
            System.out.println((i + 1) + ". " + cuentas.get(i).getNombreCliente());
        }

        System.out.print("Seleccione la cuenta origen: ");
        int origen = sc.nextInt() - 1;
        System.out.print("Seleccione la cuenta destino: ");
        int destino = sc.nextInt() - 1;
        System.out.print("Ingrese monto a transferir: ");
        double monto = sc.nextDouble();
        sc.nextLine();

        cuentas.get(origen).transferir(cuentas.get(destino), monto);
    }

    // 3️⃣ Cajero Automático
    private static void cajeroAutomatico() {
        if (cuentas.isEmpty()) {
            System.out.println("⚠️ No hay cuentas registradas.");
            return;
        }

        System.out.println("\nSeleccione la cuenta:");
        for (int i = 0; i < cuentas.size(); i++) {
            System.out.println((i + 1) + ". " + cuentas.get(i).getNombreCliente());
        }
        int indice = sc.nextInt() - 1;

        System.out.println("\n=== Cajero Automático ===");
        System.out.println("1. Retirar en cajero del banco");
        System.out.println("2. Retirar en cajero externo");
        System.out.print("Seleccione opción: ");
        int opcion = sc.nextInt();

        System.out.print("Ingrese monto a retirar: ");
        double monto = sc.nextDouble();

        boolean propio = opcion == 1;
        cuentas.get(indice).retirar(monto, propio);
    }

    // 4️⃣ Cierre de mes
    private static void cierreMes() {
        if (cuentas.isEmpty()) {
            System.out.println("⚠️ No hay cuentas registradas.");
            return;
        }

        for (Cuenta c : cuentas) {
            System.out.println("\n=== Estado de cuenta ===");
            c.mostrarInfo();
            c.calcularCierreMes();
        }
    }
}
