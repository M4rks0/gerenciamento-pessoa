package marks.gerenciamentopessoa.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import marks.gerenciamentopessoa.model.Pessoa;
import marks.gerenciamentopessoa.repository.pessoaRepository;
import marks.gerenciamentopessoa.service.EstadoCivilService;
import marks.gerenciamentopessoa.service.InstrucaoService;
import marks.gerenciamentopessoa.service.PaisService;
import marks.gerenciamentopessoa.service.PessoaService;
import marks.gerenciamentopessoa.service.RacaService;
import marks.gerenciamentopessoa.service.SexoService;
import marks.gerenciamentopessoa.service.TipoEnderecoService;

@Controller
@RequestMapping(path = "/pessoa")
public class PessoaController {

  @Autowired
  private PessoaService pessoaService;

  @Autowired
  private SexoService sexoService;

  @Autowired
  private RacaService racaService;

  @Autowired
  private PaisService paisService;

  @Autowired
  private TipoEnderecoService tipoEnderecoService;

  @Autowired
  private EstadoCivilService estadoCivilService;

  @Autowired
  private InstrucaoService instrucaoService;

  @Autowired
  private pessoaRepository pessoaRepository;

  private String TempCpf;

  @RequestMapping(path = "/novo", method = RequestMethod.GET)
  public String adicionarPessoa(RedirectAttributes attributes, Model model) {
    model.addAttribute("pessoa", new Pessoa());
    popularAtributos(model);
    return "/pessoa/cadastrar-pessoa";
  }

  @RequestMapping(path = "/salvar", method = RequestMethod.POST)
  public String salvarPessoa(@Valid Pessoa pessoa,
      BindingResult result,
      RedirectAttributes attributes,
      Model model,
      RedirectAttributes attr) {
    popularAtributos(model);
    if(!(pessoaService.save(pessoa))) {
      result.addError(new FieldError("pessoa", "cpf", "CPF já existe cadastrado!"));
    }
    if (result.hasErrors()) {
      return "/pessoa/cadastrar-pessoa";
    }
    attr.addFlashAttribute("alertIcon", "success");
    attr.addFlashAttribute("alertMessage", "Pessoa cadastrada com sucesso!");
    return "redirect:/pessoa/novo";
  }

  @RequestMapping(path = "/listar", method = RequestMethod.GET)
  public String listarPessoas(Model model) {
    model.addAttribute("pessoas", pessoaService.findAll());
    return "/pessoa/listar-pessoa";
  }

  @RequestMapping(path = "/editar/{id}", method = RequestMethod.GET)
  public String editarPessoa(@PathVariable("id") Long id, Model model, HttpSession session) {
    Pessoa pessoa = pessoaService.findById(id).get();
    session.setAttribute("id_pessoa", pessoa.getId());
    model.addAttribute("pessoa", pessoa);
    popularAtributos(model);
    return "/pessoa/cadastrar-pessoa";
  }

  @RequestMapping(path = "/atualizar/{id}", method = RequestMethod.POST)
  public String editarPessoa(@PathVariable("id") Long id, @Valid Pessoa pessoa, BindingResult result, Model model,
      RedirectAttributes attr, HttpSession session) {    
    Long id_tpm = Long.parseLong(session.getAttribute("id_pessoa").toString());
    if(!(pessoaService.save(pessoa, id_tpm))) {
      result.addError(new FieldError("pessoa", "cpf", "CPF já existe cadastrado!"));
    }
    if (result.hasErrors()) {
      return "/pessoa/cadastrar-pessoa";
    }
    pessoaService.save(pessoa, id_tpm);
    attr.addFlashAttribute("alertIcon", "success");
    attr.addFlashAttribute("alertMessage", "Pessoa editada com sucesso!");
    return "redirect:/pessoa/listar";
  }

  @RequestMapping(path = "/apagar/{id}", method = RequestMethod.GET)
  public String apagarUsuario(@PathVariable("id") Long id, Model model, RedirectAttributes attr) {
    pessoaService.remove(id);
    attr.addFlashAttribute("alertIcon", "success");
    attr.addFlashAttribute("alertMessage", "Pessoa deletada com sucesso!");
    return "redirect:/pessoa/listar";
  }

  public void popularAtributos(Model model) {
    model.addAttribute("listaSexo", sexoService.findAll());
    model.addAttribute("listaRaca", racaService.findAll());
    model.addAttribute("listaEstadoCivil", estadoCivilService.findAll());
    model.addAttribute("listaPaises", paisService.findAll());
    model.addAttribute("listaTipoEndereco", tipoEnderecoService.findAll());
    model.addAttribute("listaInstrucao", instrucaoService.findAll());
  }

}
