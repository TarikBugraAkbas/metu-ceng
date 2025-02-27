#include "components.hpp"

/* operator<< overloads */
std::ostream& operator<<(std::ostream& os, Alphabet& a){
    for(char sym : a.get_symbols())
    {
        os<< sym << " ";
    }
return os;
}
std::ostream& operator<<(std::ostream& os, const Rule& r){
    os << r.initial_state << " " << r.symbol << " " << r.final_state;
    return os;
}
std::ostream& operator<<(std::ostream& os, TransitionTable& t){
    for(int i = 0; i < t.rules.size(); i++)
    {
        os << t.rules[i] << std::endl;
    }
    return os;
}
std::ostream& operator<<(std::ostream& os, const ComputationBranch& c){
    for(int i=0; i < c.config_history.size(); i++)
    {
        os << "(" << c.config_history[i].first << "," << (c.config_history[i].second.empty() ? "e" : c.config_history[i].second) << ")"
        << (i < (c.config_history.size() - 1) ? " :- " : "");
    }
    return os;
}


/* Alphabet */
bool Alphabet::is_valid(const std::string& input) const{
    for(int i = 0; i < input.length(); i++)
    {
        if(symbols.find(input[i]) == symbols.end())
            {
                return false;
            }
    }
    return true;
}

const std::set<char> Alphabet::get_symbols() const { 
    return symbols;
}

Alphabet& Alphabet::operator+=(const Alphabet& other){
    (*this).symbols.insert(other.symbols.begin(), other.symbols.end());
    return (*this);
}


/* Rule */
Rule::Rule(const std::string& init_s, char symbol, const std::string& final_s){
    (*this).initial_state = init_s;
    (*this).symbol = symbol;
    (*this).final_state = final_s;
}

const std::string Rule::get_final_state() const {
    return (*this).final_state;
}

void Rule::update_state_name(const std::string& old_name, const std::string& new_name){
    if((*this).initial_state == old_name)
    {
        (*this).initial_state = new_name;
    }
    if((*this).final_state== old_name)
    {
        (*this).final_state = new_name;
    }

}

bool Rule::applies_to(const std::string& c, char s) const{
    if((*this).initial_state == c && (*this).symbol == s)
    {
        return true;
    }
    return false;
}


/* TransitionTable */
void TransitionTable::add_rule(const std::string& initial_state, char symbol, const std::string& final_state){
    rules.push_back(Rule(initial_state,symbol,final_state));
}

void TransitionTable::update_state_name(const std::string& old_name, const std::string& new_name){
    for(int i = 0; i < rules.size();i++)
    {
        rules[i].update_state_name(old_name,new_name);
    }
}

const std::set<std::string> TransitionTable::operator()(const std::string& curr_state, char symbol) const{
    std::set<std::string> next_states;
    for(Rule r : rules)
    {
        if(r.applies_to(curr_state,symbol))
        next_states.insert(r.get_final_state());
    }
    return next_states;
}

TransitionTable& TransitionTable::operator+=(const TransitionTable& other){
    (*this).rules.insert(rules.end(), other.rules.begin(),other.rules.end());
    return (*this);
}


/* ComputationBranch */
void ComputationBranch::push_config(const std::string& state, const std::string& input){
    (*this).config_history.push_back(std::make_pair(state,input));
}

const std::pair<std::string,std::string> ComputationBranch::get_last_config() const{
    return config_history.back();
}
