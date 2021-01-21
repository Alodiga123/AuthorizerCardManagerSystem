package com.alodiga.authorizer.cms.ws.test;

//package com.ericsson.alodiga.ws.test;
//
//import static org.junit.Assert.*;
//
//import javax.persistence.EntityManager;
//import javax.validation.ConstraintViolationException;
//
//import org.apache.log4j.Logger;
//import org.junit.Test;
//
//import com.ericsson.alodiga.model.Usuario;
//
//public class GuardarUsuarioTest {
//
//	private static final Logger logger = Logger
//			.getLogger(GuardarUsuarioTest.class);
//
//	private EntityManagerHandler emHandler = new EntityManagerHandler();
//
//	@Test
//	public void testGuardarUsuarioNuevoConExito() {
//		logger.debug("<--- testGuardarUsuarioNuevoConExito --->");
//		EntityManager em = emHandler.getEntityManager();
//		try {
//			em.getTransaction().begin();
//			Usuario usuario = Usuario
//					.guardarUsuario(em, null, "Usuario", "Prueba", "qQ111",
//							"usuario@prueba.junit", "1122334455", "13-09-1989",
//							"direccion de prueba", 1, 1, 1, "01001", null);
//			logger.debug("Inicial: " + usuario);
//			em.persist(usuario);
//			em.flush();
//			em.getTransaction().rollback();
//			logger.debug("Final: " + usuario);
//			assertTrue(usuario.toString(), usuario.getUsuarioId() != 0);
//		} catch (ConstraintViolationException e) {
//			e.printStackTrace();
//			logger.debug(e.getConstraintViolations().toString());
//			fail(e.getMessage());
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} finally {
//			em.close();
//		}
//		logger.debug("<--- testGuardarUsuarioNuevoConExito --->");
//	}
//
//	@Test
//	public void testGuardarYActualizarUsuarioConExito() {
//		logger.debug("<--- testGuardarYActualizarUsuarioConExito --->");
//		EntityManager em = emHandler.getEntityManager();
//		try {
//			em.getTransaction().begin();
//			Usuario usuario = Usuario
//					.guardarUsuario(em, null, "Usuario", "Prueba", "qQ111",
//							"usuario@prueba.junit", "1122334455", "13-09-1989",
//							"direccion de prueba", 1, 1, 1, "01001", null);
//			logger.debug("Inicial: " + usuario);
//			em.persist(usuario);
//			em.flush();
//			logger.debug("Intermedio: " + usuario);
//			assertTrue(usuario.toString(), usuario.getUsuarioId() != 0);
//			usuario.setApellido("Actualizado");
//			usuario.setCredencial("aA222");
//			em.persist(usuario);
//			em.flush();
//			em.getTransaction().rollback();
//			logger.debug("Final: " + usuario);
//			assertTrue(usuario.toString(),
//					usuario.getApellido().equals("Actualizado"));
//			assertTrue(usuario.toString(),
//					usuario.getCredencial().equals("aA222"));
//		} catch (ConstraintViolationException e) {
//			e.printStackTrace();
//			logger.debug(e.getConstraintViolations().toString());
//			fail(e.getMessage());
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} finally {
//			em.close();
//		}
//		logger.debug("<--- testGuardarYActualizarUsuarioConExito --->");
//	}
//
//	@Test
//	public void testValidarNombreNulo() {
//		logger.debug("<--- testValidarNombreNulo --->");
//		EntityManager em = emHandler.getEntityManager();
//		try {
//			em.getTransaction().begin();
//			Usuario usuario = Usuario
//					.guardarUsuario(em, null, null, "Apellido", "qQ111",
//							"usuario@prueba.junit", "1122334455", "13-09-1989",
//							"direccion de prueba", 1, 1, 1, "01001", null);
//			logger.debug("Inicial: " + usuario);
//			em.persist(usuario);
//			em.flush();
//			em.getTransaction().rollback();
//			logger.debug("Final: " + usuario);
//		} catch (ConstraintViolationException e) {
//			boolean existsValidation = ValidationMessagesHandler.messageExists(
//					"NOMBRE_NULO", e);
//			assertTrue(e.getConstraintViolations().toString(), existsValidation);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} finally {
//			em.close();
//		}
//		logger.debug("<--- testValidarNombreNulo --->");
//	}
//
//	@Test
//	public void testValidarNombresInvalidos() {
//		logger.debug("<--- testValidarNombresInvalidos --->");
//		String[] invalid = { "Nombre12", "abc!", "!)(&", "con espacios" };
//		EntityManager em = emHandler.getEntityManager();
//		try {
//			int invalidToTest = invalid.length;
//			for (String invalido : invalid) {
//				logger.debug("Probando nombre: " + invalido);
//				try {
//					em.getTransaction().begin();
//					Usuario usuario = Usuario.guardarUsuario(em, null,
//							invalido, "Apellido", "qQ111",
//							"usuario@prueba.junit", "1122334455", "13-09-1989",
//							"direccion de prueba", 1, 1, 1, "01001", null);
//					invalidToTest--;
//					em.persist(usuario);
//					em.flush();
//				} catch (ConstraintViolationException e) {
//					boolean existsValidation = ValidationMessagesHandler
//							.messageExists("NOMBRE_INVALIDO", e);
//					assertTrue(e.getConstraintViolations().toString(),
//							existsValidation);
//				} finally {
//					em.getTransaction().rollback();
//				}
//			}
//			assertEquals("No se probaron todos los nombres", 0, invalidToTest);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} finally {
//			em.close();
//		}
//		logger.debug("<--- testValidarNombresInvalidos --->");
//	}
//
//	@Test
//	public void testValidarApellidoNulo() {
//		logger.debug("<--- testValidarApellidoNulo --->");
//		EntityManager em = emHandler.getEntityManager();
//		try {
//			em.getTransaction().begin();
//			Usuario usuario = Usuario
//					.guardarUsuario(em, null, "Usuario", null, "qQ111",
//							"usuario@prueba.junit", "1122334455", "13-09-1989",
//							"direccion de prueba", 1, 1, 1, "01001", null);
//			logger.debug("Inicial: " + usuario);
//			em.persist(usuario);
//			em.flush();
//			em.getTransaction().rollback();
//			logger.debug("Final: " + usuario);
//		} catch (ConstraintViolationException e) {
//			boolean existsValidation = ValidationMessagesHandler.messageExists(
//					"APELLIDO_NULO", e);
//			assertTrue(e.getConstraintViolations().toString(), existsValidation);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} finally {
//			em.close();
//		}
//		logger.debug("<--- testValidarApellidoNulo --->");
//	}
//
//	@Test
//	public void testValidarApellidosInvalidos() {
//		logger.debug("<--- testValidarApellidosInvalidos --->");
//		String[] invalidLastnames = { "Apellido12", "abc!", "!)(&",
//				"con espacios" };
//		EntityManager em = emHandler.getEntityManager();
//		try {
//			int invalid = invalidLastnames.length;
//			for (String invalido : invalidLastnames) {
//				logger.debug("Probando apellido: " + invalido);
//				try {
//					em.getTransaction().begin();
//					Usuario usuario = Usuario.guardarUsuario(em, null,
//							"Usuario", invalido, "qQ111",
//							"usuario@prueba.junit", "1122334455", "13-09-1989",
//							"direccion de prueba", 1, 1, 1, "01001", null);
//					invalid--;
//					em.persist(usuario);
//					em.flush();
//				} catch (ConstraintViolationException e) {
//					boolean existsValidation = ValidationMessagesHandler
//							.messageExists("APELLIDO_INVALIDO", e);
//					assertTrue(e.getConstraintViolations().toString(),
//							existsValidation);
//				} finally {
//					em.getTransaction().rollback();
//				}
//			}
//			assertEquals("No se probaron todos los apellidos", 0, invalid);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} finally {
//			em.close();
//		}
//		logger.debug("<--- testValidarApellidosInvalidos --->");
//	}
//
//	@Test
//	public void testValidarCredencialNula() {
//		logger.debug("<--- testValidarCredencialNula --->");
//		EntityManager em = emHandler.getEntityManager();
//		try {
//			em.getTransaction().begin();
//			Usuario usuario = Usuario
//					.guardarUsuario(em, null, "Usuario", "Apellido", null,
//							"usuario@prueba.junit", "1122334455", "13-09-1989",
//							"direccion de prueba", 1, 1, 1, "01001", null);
//			logger.debug("Inicial: " + usuario);
//			em.persist(usuario);
//			em.flush();
//			em.getTransaction().rollback();
//			logger.debug("Final: " + usuario);
//		} catch (ConstraintViolationException e) {
//			boolean existsValidation = ValidationMessagesHandler.messageExists(
//					"CONTRASENIA_NULA", e);
//			assertTrue(e.getConstraintViolations().toString(), existsValidation);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} finally {
//			em.close();
//		}
//		logger.debug("<--- testValidarCredencialNula --->");
//	}
//
//	@Test
//	public void testValidarCredencialesInvalidas() {
//		logger.debug("<--- testValidarCredencialesInvalidas --->");
//		String[] invalid = { "abcd", "a!1A", "123!%", "AA122" };
//		EntityManager em = emHandler.getEntityManager();
//		try {
//			int invalidLastnamesToTest = invalid.length;
//			for (String credencial : invalid) {
//				logger.debug("Probando credencial: " + credencial);
//				try {
//					em.getTransaction().begin();
//					Usuario usuario = Usuario.guardarUsuario(em, null,
//							"Usuario", "Apellido", credencial,
//							"usuario@prueba.junit", "1122334455", "13-09-1989",
//							"direccion de prueba", 1, 1, 1, "01001", null);
//					invalidLastnamesToTest--;
//					em.persist(usuario);
//					em.flush();
//				} catch (ConstraintViolationException e) {
//					boolean existsValidation = ValidationMessagesHandler
//							.messageExists("CONTRASENIA_INVALIDA", e);
//					assertTrue(e.getConstraintViolations().toString(),
//							existsValidation);
//				} finally {
//					em.getTransaction().rollback();
//				}
//			}
//			assertEquals("No se probaron todos las credenciales", 0,
//					invalidLastnamesToTest);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} finally {
//			em.close();
//		}
//		logger.debug("<--- testValidarCredencialesInvalidas --->");
//	}
//
//	@Test
//	public void testValidarEmailNulo() {
//		logger.debug("<--- testValidarEmailNulo --->");
//		EntityManager em = emHandler.getEntityManager();
//		try {
//			em.getTransaction().begin();
//			Usuario usuario = Usuario.guardarUsuario(em, null, "Usuario",
//					"Apellido", "qQ111", null, "1122334455", "13-09-1989",
//					"direccion de prueba", 1, 1, 1, "01001", null);
//			logger.debug("Inicial: " + usuario);
//			em.persist(usuario);
//			em.flush();
//			em.getTransaction().rollback();
//			logger.debug("Final: " + usuario);
//		} catch (ConstraintViolationException e) {
//			boolean existsValidation = ValidationMessagesHandler.messageExists(
//					"EMAIL_NULO", e);
//			assertTrue(e.getConstraintViolations().toString(), existsValidation);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} finally {
//			em.close();
//		}
//		logger.debug("<--- testValidarEmailNulo --->");
//	}
//
//	@Test
//	public void testValidarEmailInvalidos() {
//		logger.debug("<--- testValidarEmailInvalidos --->");
//		String[] invalid = { "a@a", "@email.com", "ab!@mail.com" };
//		EntityManager em = emHandler.getEntityManager();
//		try {
//			int invalidLastnamesToTest = invalid.length;
//			for (String invalidValue : invalid) {
//				logger.debug("Probando email: " + invalidValue);
//				try {
//					em.getTransaction().begin();
//					Usuario usuario = Usuario.guardarUsuario(em, null,
//							"Usuario", "Apellido", "qQ111", invalidValue,
//							"1122334455", "13-09-1989", "direccion de prueba",
//							1, 1, 1, "01001", null);
//					invalidLastnamesToTest--;
//					em.persist(usuario);
//					em.flush();
//				} catch (ConstraintViolationException e) {
//					boolean existsValidation = ValidationMessagesHandler
//							.messageExists("EMAIL_INVALIDO", e);
//					assertTrue(e.getConstraintViolations().toString(),
//							existsValidation);
//				} finally {
//					em.getTransaction().rollback();
//				}
//			}
//			assertEquals("No se probaron todos las emails", 0,
//					invalidLastnamesToTest);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} finally {
//			em.close();
//		}
//		logger.debug("<--- testValidarEmailInvalidos --->");
//	}
//
//	@Test
//	public void testValidarEdadesInvalidas() {
//		logger.debug("<--- testValidarEdadesInvalidas --->");
//		String[] invalid = { "01-01-1950", "15-08-2010" };
//		EntityManager em = emHandler.getEntityManager();
//		try {
//			int invalidLastnamesToTest = invalid.length;
//			for (String invalidValue : invalid) {
//				logger.debug("Probando fecha: " + invalidValue);
//				try {
//					em.getTransaction().begin();
//					Usuario usuario = Usuario.guardarUsuario(em, null,
//							"Usuario", "Apellido", "qQ111",
//							"usuario@prueba.junit", "1122334455", invalidValue,
//							"direccion de prueba", 1, 1, 1, "01001", null);
//					invalidLastnamesToTest--;
//					em.persist(usuario);
//					em.flush();
//				} catch (ConstraintViolationException e) {
//					boolean existsValidation = ValidationMessagesHandler
//							.messageExists("FECHA_NACIMIENTO_INVALIDA", e);
//					assertTrue(e.getConstraintViolations().toString(),
//							existsValidation);
//				} finally {
//					em.getTransaction().rollback();
//				}
//			}
//			assertEquals("No se probaron todos las edades", 0,
//					invalidLastnamesToTest);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} finally {
//			em.close();
//		}
//		logger.debug("<--- testValidarEdadesInvalidas --->");
//	}
//
//	@Test
//	public void testValidarTelefonoNulo() {
//		logger.debug("<--- testValidarTelefonoNulo --->");
//		EntityManager em = emHandler.getEntityManager();
//		try {
//			em.getTransaction().begin();
//			Usuario usuario = Usuario
//					.guardarUsuario(em, null, "Usuario", "Apellido", "qQ111",
//							"usuario@prueba.junit", null, "13-09-1989",
//							"direccion de prueba", 1, 1, 1, "01001", null);
//			logger.debug("Inicial: " + usuario);
//			em.persist(usuario);
//			em.flush();
//			em.getTransaction().rollback();
//			logger.debug("Final: " + usuario);
//		} catch (ConstraintViolationException e) {
//			boolean existsValidation = ValidationMessagesHandler.messageExists(
//					"NUMERO_DE_TELEFONO_NULO", e);
//			assertTrue(e.getConstraintViolations().toString(), existsValidation);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} finally {
//			em.close();
//		}
//		logger.debug("<--- testValidarTelefonoNulo --->");
//	}
//
//	@Test
//	public void testValidarTelefonosInvalidos() {
//		logger.debug("<--- testValidarTelefonosInvalidos --->");
//		String[] invalid = { "a@a", "123456789", "12345678901234567" };
//		EntityManager em = emHandler.getEntityManager();
//		try {
//			int invalidToTest = invalid.length;
//			for (String invalidValue : invalid) {
//				logger.debug("Probando email: " + invalidValue);
//				try {
//					em.getTransaction().begin();
//					Usuario usuario = Usuario.guardarUsuario(em, null,
//							"Usuario", "Apellido", "qQ111",
//							"usuario@prueba.junit", invalidValue, "13-09-1989",
//							"direccion de prueba", 1, 1, 1, "01001", null);
//					invalidToTest--;
//					em.persist(usuario);
//					em.flush();
//				} catch (ConstraintViolationException e) {
//					boolean existsValidation = ValidationMessagesHandler
//							.messageExists("NUMERO_DE_TELEFONO_INVALIDO", e);
//					assertTrue(e.getConstraintViolations().toString(),
//							existsValidation);
//				} finally {
//					em.getTransaction().rollback();
//				}
//			}
//			assertEquals("No se probaron todos los telefonos", 0, invalidToTest);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		} finally {
//			em.close();
//		}
//		logger.debug("<--- testValidarTelefonosInvalidos --->");
//	}
//}
